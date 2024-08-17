package wxdgaming.spring.boot.rpc;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RPC {

    /** 路由映射 */
    String value() default "";

}
