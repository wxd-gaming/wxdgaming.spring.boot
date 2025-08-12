package wxdgaming.spring.boot.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.Order;
import wxdgaming.spring.boot.core.ann.Init;
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
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-29 14:37
 **/
@Slf4j
@Getter
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
    protected ApplicationContext applicationContext;

    protected volatile List<Content<Object>> beans;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void executorInitWithMethodAnnotated() {
        executorWithMethodAnnotated(Init.class);
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
        methodStream = methodStream.sorted();
        return methodStream;
    }

    public void executorWithMethodAnnotated(Class<? extends Annotation> annotation, Object... args) {
        Stream<MethodContent> methodContentStream = withMethodAnnotated(annotation);
        methodContentStream.forEach(methodContent -> methodContent.invoke(args));
    }

    public void executorWithMethodAnnotatedIgnoreException(Class<? extends Annotation> annotation, Object... args) {
        Stream<MethodContent> methodContentStream = withMethodAnnotated(annotation);
        methodContentStream.forEach(methodContent -> {
            try {
                methodContent.invoke(args);
            } catch (Exception e) {
                log.error("执行方法异常", e);
            }
        });
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

        @Override
        public String toString() {
            return instance.getClass().getName();
        }
    }

    @Getter
    public class MethodContent implements Comparable<MethodContent> {

        private final Object bean;
        private final Method method;

        public MethodContent(Object bean, Method method) {
            this.bean = bean;
            this.method = method;
        }

        @Override
        public String toString() {
            return "MethodContent{bean=%s, method=%s}".formatted(bean.getClass().getName(), method.getName());
        }

        public Object invoke(Object... args) {
            try {
                log.debug("{}.{}", bean.getClass().getSimpleName(), this.method.getName());
                Object[] objects = ApplicationContextProvider.this.springParameters(bean, method, args);
                return method.invoke(bean, objects);
            } catch (Exception e) {
                throw Throw.of(toString(), e);
            }
        }

        @Override
        public int compareTo(MethodContent o) {

            int o1Sort = AnnUtil.annOpt(method, Order.class)
                    .or(() -> AnnUtil.annOpt(bean.getClass(), Order.class))
                    .map(Order::value)
                    .orElse(999999);

            int o2Sort = AnnUtil.annOpt(o.method, Order.class)
                    .or(() -> AnnUtil.annOpt(o.bean.getClass(), Order.class))
                    .map(Order::value)
                    .orElse(999999);

            if (o1Sort == o2Sort) {
                /*如果排序值相同，采用名字排序*/
                return method.getName().compareTo(o.method.getName());
            }
            return Integer.compare(o1Sort, o2Sort);
        }
    }

    /** 传递参数提取 */
    static class HolderArgument {

        private final Object[] arguments;
        private int argumentIndex = 0;

        public HolderArgument(Object[] arguments) {
            this.arguments = arguments;
        }

        @SuppressWarnings("unchecked")
        public <R> R next() {
            return (R) arguments[argumentIndex++];
        }

    }

    public Object[] springParameters(Object bean, Method method, Object... args) {
        Parameter[] parameters = method.getParameters();
        Object[] params = new Object[parameters.length];
        HolderArgument holderArgument = new HolderArgument(args);
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
                if (StringUtils.isBlank(name))
                    params[i] = applicationContext.getBean(parameterType);
                else
                    params[i] = applicationContext.getBean(name);
                continue;
            }
            Value value = parameter.getAnnotation(Value.class);
            if (value != null) {
                params[i] = configValue(value, parameterizedType);
                continue;
            }
            params[i] = holderArgument.next();
        }
        return params;
    }

    public Object configValue(Value value, Type parameterizedType) {
        if (value != null) {
            String valueKey = value.value();
            return configValue(valueKey, parameterizedType);
        }
        return null;
    }

    public Object configValue(String valueKey, Type parameterizedType) {
        Object o;
        if (valueKey.startsWith("${")) {
            o = applicationContext.getEnvironment().resolvePlaceholders(valueKey);

        } else {
            o = applicationContext.getEnvironment().getProperty(valueKey);
        }
        if (parameterizedType.equals(String.class)) {
            return o;
        } else {
            return FastJsonUtil.parse(String.valueOf(o), parameterizedType);
        }
    }
}
