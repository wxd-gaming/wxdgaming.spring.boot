package wxdgaming.spring.boot.core;

import com.alibaba.fastjson.util.TypeUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import wxdgaming.spring.boot.core.ann.AppStart;
import wxdgaming.spring.boot.core.ann.LogicStart;
import wxdgaming.spring.boot.core.ann.ReLoad;
import wxdgaming.spring.boot.core.lang.Tuple2;
import wxdgaming.spring.boot.core.system.AnnUtil;
import wxdgaming.spring.boot.core.system.FieldUtil;
import wxdgaming.spring.boot.core.system.MethodUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 资源处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-10-16 10:11
 **/
@Getter
public class SpringReflectContent {

    public static final Comparator<Object> OBJECT_COMPARATOR = (o1, o2) -> {
        int o1Annotation = Optional.ofNullable(o1.getClass().getAnnotation(Order.class)).map(Order::value).orElse(999999);
        int o2Annotation = Optional.ofNullable(o2.getClass().getAnnotation(Order.class)).map(Order::value).orElse(999999);
        if (o1Annotation != o2Annotation) {
            return Integer.compare(o1Annotation, o2Annotation);
        }
        return o1.getClass().getName().compareTo(o2.getClass().getName());
    };

    public static final Comparator<Tuple2<?, Method>> METHOD_COMPARATOR = (o1, o2) -> {
        int o1Annotation = Optional.ofNullable(o1.getRight().getAnnotation(Order.class)).map(Order::value).orElse(999999);
        int o2Annotation = Optional.ofNullable(o2.getRight().getAnnotation(Order.class)).map(Order::value).orElse(999999);
        if (o1Annotation != o2Annotation) {
            return Integer.compare(o1Annotation, o2Annotation);
        }
        return o1.getRight().getName().compareTo(o2.getRight().getName());
    };

    /** 所有的bean */
    public static Stream<Object> getBeans(ApplicationContext applicationContext) {
        Stream<Object> parent = Stream.empty();
        if (applicationContext.getParent() != null) {
            parent = getBeans(applicationContext.getParent());
        }
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        Stream<Object> objectStream = Arrays.stream(beanDefinitionNames).map(applicationContext::getBean);
        return Stream.concat(parent, objectStream).sorted(OBJECT_COMPARATOR);
    }

    private final ApplicationContext applicationContext;
    /** 所有的类 */
    private final List<Content<Object>> instanceList;

    public SpringReflectContent(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.instanceList = getBeans(applicationContext).map(Content::new).sorted(OBJECT_COMPARATOR).toList();
    }

    public Stream<Content<Object>> stream() {
        return instanceList.stream();
    }

    /** 执行{@link AppStart}注解方法 */
    public void executorAppStartMethod() {
        executorMethod(AppStart.class);
    }

    /** 执行{@link LogicStart}注解方法 */
    public void executorLogicStartMethod() {
        executorMethod(LogicStart.class);
    }

    /** 执行{@link ReLoad}注解方法 */
    public void executorReloadMethod() {
        executorMethod(ReLoad.class);
    }

    /** 调用执行含有指定注解的方法 */
    public void executorMethod(Class<? extends Annotation> annotationType) {
        withMethodAnnotated(annotationType)
                .sorted(METHOD_COMPARATOR)
                .forEach(t -> {
                    try {
                        t.getRight().setAccessible(true);
                        Object[] array = springParameters(t.getLeft(), t.getRight());
                        // Object[] array = Arrays.stream(t.getRight().getParameterTypes()).map(curApplicationContext()::getBean).toArray();
                        t.getRight().invoke(t.getLeft(), array);
                    } catch (Exception e) {
                        throw new RuntimeException(t.getLeft().getClass().getName() + "#" + t.getRight().getName(), e);
                    }
                });
    }

    public Object[] springParameters(Object bean, Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Type type = parameter.getParameterizedType();
            if (type instanceof Class<?> clazz) {
                if (SpringReflectContent.class.isAssignableFrom(clazz)) {
                    params[i] = clazz.cast(this);
                    continue;
                }
                /*实现注入*/
                Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
                if (qualifier != null) {
                    String name = qualifier.value();
                    if (StringsUtil.emptyOrNull(name)) {
                        throw new RuntimeException(bean.getClass().getName() + "#" + method.getName() + ", 无法识别 " + (i + 1) + " 参数 RequestParam 指定 name " + clazz);
                    }
                    params[i] = applicationContext.getBean(name);
                    continue;
                }
                Value value = parameter.getAnnotation(Value.class);
                if (value != null) {
                    String valueKey = value.value();

                    Object o;
                    if (valueKey.startsWith("${")) {
                        String v2 = applicationContext.getEnvironment().resolvePlaceholders(valueKey);
                        o = TypeUtils.castToJavaBean(v2, clazz);
                    } else {
                        o = applicationContext.getEnvironment().getProperty(valueKey, clazz);
                    }
                    params[i] = o;
                    continue;
                }
                params[i] = applicationContext.getBean(clazz);
            }
        }
        return params;
    }

    /** 父类或者接口 */
    public <U> Stream<U> withSuper(Class<U> cls) {
        return withSuper(cls, null);
    }

    /** 父类或者接口 */
    public <U> Stream<U> withSuper(Class<U> cls, Predicate<U> predicate) {
        @SuppressWarnings("unchecked")
        Stream<U> tmp = stream()
                .map(v -> v.instance)
                .filter(e -> cls.isAssignableFrom(e.getClass()))
                .map(c -> (U) c);
        if (predicate != null) tmp = tmp.filter(predicate);
        return tmp;
    }

    /** 所有添加了这个注解的类 */
    public Stream<Object> withAnnotated(Class<? extends Annotation> annotation) {
        return withAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Object> withAnnotated(Class<? extends Annotation> annotation, Predicate<Object> predicate) {
        Stream<Object> tmp = stream()
                .map(v -> v.instance)
                .filter(c -> AnnUtil.ann(c.getClass(), annotation) != null);
        if (predicate != null) tmp = tmp.filter(predicate);
        return tmp;
    }

    /** 所有bean里面的方法，添加了注解的 */
    public Stream<Tuple2<Object, Method>> withMethodAnnotated(Class<? extends Annotation> annotation) {
        return withMethodAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Tuple2<Object, Method>> withMethodAnnotated(Class<? extends Annotation> annotation, Predicate<Tuple2<Object, Method>> predicate) {
        Stream<Tuple2<Object, Method>> methodStream = stream()
                .flatMap(info -> info.methodsWithAnnotated(annotation)
                        .map(m -> new Tuple2<>(info.instance, m)));
        if (predicate != null) {
            methodStream = methodStream.filter(predicate);
        }
        return methodStream;
    }

    @Getter
    public static class Content<T> {

        private final T instance;

        public static <U> Content<U> of(U cls) {
            return new Content<>(cls);
        }

        Content(T instance) {
            this.instance = instance;
        }

        /** 是否添加了注解 */
        public boolean withAnnotated(Class<? extends Annotation> annotation) {
            return AnnUtil.ann(instance.getClass(), annotation) != null;
        }

        /** 所有的方法 */
        public Collection<Method> getMethods() {
            return MethodUtil.readAllMethod(instance.getClass()).values();
        }

        /** 所有的方法 */
        public Stream<Method> methodStream() {
            return getMethods().stream();
        }

        /** 所有添加了这个注解的方法 */
        public Stream<Method> methodsWithAnnotated(Class<? extends Annotation> annotation) {
            return methodStream().filter(m -> AnnUtil.ann(m, annotation) != null);
        }

        /** 所有的字段 */
        public Collection<Field> getFields() {
            return FieldUtil.getFields(false, instance.getClass()).values();
        }

        /** 所有的字段 */
        public Stream<Field> fieldStream() {
            return getFields().stream();
        }

        /** 所有添加了这个注解的字段 */
        public Stream<Field> fieldWithAnnotated(Class<? extends Annotation> annotation) {
            return fieldStream().filter(f -> AnnUtil.ann(f, annotation) != null);
        }

        @Override public String toString() {
            return instance.getClass().getName();
        }
    }

}
