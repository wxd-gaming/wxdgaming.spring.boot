package wxdgaming.spring.boot.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.reflect.AnnUtil;
import wxdgaming.spring.boot.core.reflect.FieldUtil;
import wxdgaming.spring.boot.core.reflect.MethodUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * ApplicationContext 持有者
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-29 14:37
 **/
@Slf4j
@Getter
@Component
public class ApplicationContextProvider implements InitPrint, ApplicationContextAware {

    public static final Comparator<Object> OBJECT_COMPARATOR = (o1, o2) -> {
        int o1Annotation = Optional.ofNullable(o1.getClass().getAnnotation(Order.class)).map(Order::value).orElse(999999);
        int o2Annotation = Optional.ofNullable(o2.getClass().getAnnotation(Order.class)).map(Order::value).orElse(999999);
        if (o1Annotation != o2Annotation) {
            return Integer.compare(o1Annotation, o2Annotation);
        }
        return o1.getClass().getName().compareTo(o2.getClass().getName());
    };

    /** 上下文对象实例 */
    private ApplicationContext applicationContext;

    private volatile List<Content<Object>> beans;

    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        SpringUtil.applicationContext = this;
    }

    public List<Content<Object>> getBeans() {
        if (beans == null) {
            synchronized (this) {
                if (beans == null) {
                    beans = buildBeans(applicationContext).map(Content::new).toList();
                }
            }
        }
        return beans;
    }

    /** 所有的bean */
    private Stream<Object> buildBeans(ApplicationContext __applicationContext) {
        Stream<Object> parent = Stream.empty();
        if (__applicationContext.getParent() != null) {
            parent = buildBeans(__applicationContext.getParent());
        }
        String[] beanDefinitionNames = __applicationContext.getBeanDefinitionNames();
        Stream<Object> objectStream = Arrays.stream(beanDefinitionNames).map(__applicationContext::getBean);
        return Stream.concat(parent, objectStream).sorted(OBJECT_COMPARATOR);
    }

    public Stream<Content<Object>> stream() {
        return getBeans().stream();
    }

    /** 父类或者接口 */
    public <U> Stream<U> classWithSuper(Class<U> cls) {
        return classWithSuper(cls, null);
    }

    /** 父类或者接口 */
    public <U> Stream<U> classWithSuper(Class<U> cls, Predicate<U> predicate) {
        Stream<U> tmp = stream()
                .filter(content -> content.withSuper(cls))
                .map(content -> cls.cast(content.instance));
        if (predicate != null) tmp = tmp.filter(predicate);
        return tmp;
    }

    /** 所有添加了这个注解的类 */
    public Stream<Object> classWithAnnotated(Class<? extends Annotation> annotation) {
        return classWithAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Object> classWithAnnotated(Class<? extends Annotation> annotation, Predicate<Object> predicate) {
        Stream<Object> tmp = stream()
                .filter(content -> content.withAnnotated(annotation))
                .map(v -> v.instance);
        if (predicate != null) tmp = tmp.filter(predicate);
        return tmp;
    }

    /** 父类或者接口 */
    public <U> Stream<Content<U>> withSuper(Class<U> cls) {
        return withSuper(cls, null);
    }

    /** 父类或者接口 */
    public <U> Stream<Content<U>> withSuper(Class<U> cls, Predicate<Content<U>> predicate) {
        @SuppressWarnings("unchecked")
        Stream<Content<U>> tmp = stream()
                .filter(content -> content.withSuper(cls))
                .map(content -> (Content<U>) content);
        if (predicate != null) tmp = tmp.filter(predicate);
        return tmp;
    }

    /** 父类或者接口 */
    public Stream<Content<Object>> withAnnotated(Class<? extends Annotation> annotation) {
        return withAnnotated(annotation, null);
    }

    /** 父类或者接口 */
    public Stream<Content<Object>> withAnnotated(Class<? extends Annotation> annotation, Predicate<Content<Object>> predicate) {
        Stream<Content<Object>> tmp = stream().filter(content -> content.withAnnotated(annotation));
        if (predicate != null) tmp = tmp.filter(predicate);
        return tmp;
    }

    /** 所有bean里面的方法，添加了注解的 */
    public Stream<MethodContent> withMethodAnnotated(Class<? extends Annotation> annotation) {
        return withMethodAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<MethodContent> withMethodAnnotated(Class<? extends Annotation> annotation, Predicate<MethodContent> predicate) {
        Stream<MethodContent> methodStream = stream().flatMap(info -> info.methodsWithAnnotated(annotation));
        if (predicate != null) {
            methodStream = methodStream.filter(predicate);
        }
        return methodStream;
    }

    @Getter
    public class Content<T> {

        private final T instance;
        /** 所有的字段 */
        private final Collection<Field> fields;
        /** 所有的方法 */
        private final Collection<MethodContent> methods;


        Content(T instance) {
            this.instance = instance;
            /*缓存起来，因为反射很耗时*/
            this.fields = Collections.unmodifiableCollection(FieldUtil.getFields(false, instance.getClass()).values());
            Collection<Method> values = MethodUtil.readAllMethod(false, instance.getClass()).values();
            this.methods = values.stream().map(m -> new MethodContent(instance, m)).toList();
        }

        /** 是否添加了注解 */
        public boolean withAnnotated(Class<? extends Annotation> annotation) {
            return AnnUtil.ann(instance.getClass(), annotation) != null;
        }

        /** 是否添加了注解 */
        public boolean withSuper(Class<?> cls) {
            return cls.isAssignableFrom(instance.getClass());
        }

        /** 所有的方法 */
        public Stream<MethodContent> methodStream() {
            return getMethods().stream();
        }

        /** 所有添加了这个注解的方法 */
        public Stream<MethodContent> methodsWithAnnotated(Class<? extends Annotation> annotation) {
            return methodStream().filter(m -> AnnUtil.ann(m.getMethod(), annotation) != null);
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

    @Getter
    public class MethodContent implements Comparable<MethodContent> {

        private final Object ins;
        private final Method method;

        public MethodContent(Object ins, Method method) {
            this.ins = ins;
            this.method = method;
        }

        public Object invoke() {
            try {
                log.debug("{}.{}", ins.getClass().getSimpleName(), this.method.getName());
                Object[] objects = ApplicationContextProvider.this.springParameters(ins, method);
                return method.invoke(ins, objects);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        @Override public int compareTo(MethodContent o) {

            int o1Sort = AnnUtil.annOpt(method, Order.class)
                    .or(() -> AnnUtil.annOpt(ins.getClass(), Order.class))
                    .map(Order::value)
                    .orElse(999999);

            int o2Sort = AnnUtil.annOpt(o.method, Order.class)
                    .or(() -> AnnUtil.annOpt(o.ins.getClass(), Order.class))
                    .map(Order::value)
                    .orElse(999999);

            if (o1Sort == o2Sort) {
                /*如果排序值相同，采用名字排序*/
                return method.getName().compareTo(o.method.getName());
            }
            return Integer.compare(o1Sort, o2Sort);
        }
    }

    public Object[] springParameters(Object bean, Method method) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();
            Type parameterizedType = parameter.getParameterizedType();
            if (ApplicationContextProvider.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(this);
                continue;
            } else if (ApplicationContext.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(applicationContext);
                continue;
            }
            /*实现注入*/
            Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
            if (qualifier != null) {
                String name = qualifier.value();
                if (StringUtils.isBlank(name)) {
                    throw new RuntimeException(
                            "%s#%s, 无法识别 %d 参数 RequestParam 指定 name %s"
                                    .formatted(bean.getClass().getName(), method.getName(), i + 1, parameterType)
                    );
                }
                params[i] = applicationContext.getBean(name);
                continue;
            }
            Value value = parameter.getAnnotation(Value.class);
            if (value != null) {
                params[i] = configValue(value, parameterizedType);
                continue;
            }
            params[i] = applicationContext.getBean(parameterType);
        }
        return params;
    }

    public Object configValue(Value value, Type parameterizedType) {
        if (value != null) {
            String valueKey = value.value();
            Object o;
            if (valueKey.startsWith("${")) {
                String v2 = applicationContext.getEnvironment().resolvePlaceholders(valueKey);
                o = FastJsonUtil.parse(v2, parameterizedType);
            } else {
                String property = applicationContext.getEnvironment().getProperty(valueKey);
                o = FastJsonUtil.parse(property, parameterizedType);
            }
            return o;
        }
        return null;
    }
}
