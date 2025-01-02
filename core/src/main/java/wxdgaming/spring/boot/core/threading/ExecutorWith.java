package wxdgaming.spring.boot.core.threading;

import java.lang.annotation.*;

/**
 * 执行者详情
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-01-02 11:47
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.MODULE)
public @interface ExecutorWith {

    String threadName() default "logic";

    String queueName() default "";

}
