package wxdgaming.spring.boot.rpc;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RPC {

    boolean checkToken() default true;

    /** 路由映射 */
    String value() default "";

    String threadName() default "logic";

    String queueName() default "";

}
