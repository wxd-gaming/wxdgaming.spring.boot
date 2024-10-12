package wxdgaming.spring.boot.core;

import lombok.Getter;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.core.lang.Tuple2;
import wxdgaming.spring.boot.core.system.AnnUtil;
import wxdgaming.spring.boot.core.system.FieldUtil;
import wxdgaming.spring.boot.core.system.MethodUtil;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * 资源处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-10-16 10:11
 **/
@Getter
public class SpringReflectContext {

    public static Stream<Object> getBeans(ConfigurableApplicationContext applicationContext) {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        Stream<String> stream = Arrays.stream(beanDefinitionNames);
        if (applicationContext.getParent() != null) {
            String[] beanDefinitionNames1 = applicationContext.getParent().getBeanDefinitionNames();
            stream = Stream.concat(stream, Arrays.stream(beanDefinitionNames1));
        }

        return stream
                .map(applicationContext::getBean)
                .sorted(SpringUtil.OBJECT_COMPARATOR);
    }

    public static SpringReflectContext build(ConfigurableApplicationContext applicationContext) {
        return new SpringReflectContext(getBeans(applicationContext));
    }

    /** 所有的类 */
    private final Stream<Object> instanceList;

    public SpringReflectContext(Stream<Object> instanceList) {
        this.instanceList = instanceList;
    }

    /** 父类或者接口 */
    public <U> Stream<U> classWithSuper(Class<U> cls) {
        return classWithSuper(cls, null);
    }

    /** 父类或者接口 */
    public <U> Stream<U> classWithSuper(Class<U> cls, Predicate<U> predicate) {
        @SuppressWarnings("unchecked")
        Stream<U> tmp = instanceList.filter(e -> cls.isAssignableFrom(e.getClass())).map(c -> (U) c);
        if (predicate != null) tmp = tmp.filter(predicate);
        return tmp;
    }

    /** 所有添加了这个注解的类 */
    public Stream<Object> classWithAnnotated(Class<? extends Annotation> annotation) {
        return classWithAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Object> classWithAnnotated(Class<? extends Annotation> annotation, Predicate<Object> predicate) {
        Stream<Object> tmp = instanceList.filter(c -> AnnUtil.ann(c.getClass(), annotation) != null);
        if (predicate != null) tmp = tmp.filter(predicate);
        return tmp;
    }

    public Stream<Content<Object>> stream() {
        return instanceList.map(Content::new);
    }

    /** 父类或者接口 */
    public <U> Stream<Content<U>> withSuper(Class<U> cls) {
        return withSuper(cls, null);
    }

    /** 父类或者接口 */
    public <U> Stream<Content<U>> withSuper(Class<U> cls, Predicate<U> predicate) {
        return classWithSuper(cls, predicate).map(Content::new);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Content<Object>> withAnnotated(Class<? extends Annotation> annotation) {
        return withAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Content<Object>> withAnnotated(Class<? extends Annotation> annotation, Predicate<Object> predicate) {
        return classWithAnnotated(annotation, predicate).map(Content::new);
    }

    /** 所有bean里面的方法，添加了注解的 */
    public Stream<Tuple2<Object, Method>> withMethodAnnotated(Class<? extends Annotation> annotation) {
        return withMethodAnnotated(annotation, null);
    }

    /** 所有添加了这个注解的类 */
    public Stream<Tuple2<Object, Method>> withMethodAnnotated(Class<? extends Annotation> annotation, Predicate<Tuple2<Object, Method>> predicate) {
        Stream<Tuple2<Object, Method>> methodStream = stream()
                .flatMap(info -> info.methodsWithAnnotated(annotation).map(m -> new Tuple2<>(info.instance, m)));
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

    }

}
