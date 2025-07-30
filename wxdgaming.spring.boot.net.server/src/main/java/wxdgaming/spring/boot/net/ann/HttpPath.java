package wxdgaming.spring.boot.net.ann;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;

/**
 * http 请求注解
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-10 08:57
 */
@Documented
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({
        ElementType.PARAMETER,
})
public @interface HttpPath {

    String value();

}
