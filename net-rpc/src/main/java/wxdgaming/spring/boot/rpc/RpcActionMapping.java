package wxdgaming.spring.boot.rpc;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.core.lang.ObjectBase;

import java.lang.reflect.Method;

@Getter
@Setter
class RpcActionMapping extends ObjectBase {

    RPC annotation;
    Object bean;
    Method method;

    public RpcActionMapping(RPC annotation, Object bean, Method method) {
        this.annotation = annotation;
        this.bean = bean;
        this.method = method;
    }
}
