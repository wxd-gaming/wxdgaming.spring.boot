package wxdgaming.spring.boot.rpc;

import lombok.Data;

import java.lang.reflect.Method;

@Data
class RpcActionMapping {

    Object bean;
    Method method;

    public RpcActionMapping(Method method, Object bean) {
        this.method = method;
        this.bean = bean;
    }

}
