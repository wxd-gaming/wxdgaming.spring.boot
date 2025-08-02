package wxdgaming.spring.boot.core.ann;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;

@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
public @interface Named {

    String value() default "";

    boolean required() default true;
}
