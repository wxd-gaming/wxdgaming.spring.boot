package wxdgaming.spring.boot.starter.core.ann;

import java.lang.annotation.*;

/** 特殊读取使用的备注 */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Comment {

    String value();

}
