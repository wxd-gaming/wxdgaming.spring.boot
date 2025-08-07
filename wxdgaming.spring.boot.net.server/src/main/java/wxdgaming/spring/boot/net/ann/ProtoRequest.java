package wxdgaming.spring.boot.net.ann;

import java.lang.annotation.Documented;

/**
 * proto请求
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 11:40
 */
@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({
        java.lang.annotation.ElementType.METHOD,
})
public @interface ProtoRequest {

    /** 备注 */
    String comment() default "";

    /** 特殊用法，忽略队列效果 */
    boolean ignoreQueue() default false;
}
