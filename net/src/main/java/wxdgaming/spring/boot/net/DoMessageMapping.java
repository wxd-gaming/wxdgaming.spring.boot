package wxdgaming.spring.boot.net;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.reflect.Method;

/**
 * 处理映射
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-19 19:51
 **/
@Data
@AllArgsConstructor
public class DoMessageMapping {

    private Object bean;
    private Method method;
    private Class<?> messageType;

}
