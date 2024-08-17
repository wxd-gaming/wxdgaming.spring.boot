package wxdgaming.spring.boot.net;

import java.lang.annotation.*;

/**
 * 消息通信处理器映射注解
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-17 11:31
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MsgMapper {

    int moduleId() default 0;

    int funId() default 0;

    int msgId() default 0;

}
