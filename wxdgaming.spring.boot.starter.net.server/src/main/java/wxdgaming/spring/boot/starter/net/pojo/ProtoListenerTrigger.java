package wxdgaming.spring.boot.starter.net.pojo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import wxdgaming.spring.boot.starter.core.reflect.ReflectContext;
import wxdgaming.spring.boot.starter.core.SpringReflectContent;
import wxdgaming.spring.boot.starter.core.threading.Event;
import wxdgaming.spring.boot.starter.net.SocketSession;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * 事件触发器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 08:45
 **/
@Slf4j
@Getter
public class ProtoListenerTrigger extends Event {

    private final ProtoMapping protoMapping;
    private final SpringReflectContent runApplication;
    private final SocketSession socketSession;
    private final int messageId;
    private final PojoBase pojoBase;

    public ProtoListenerTrigger(ProtoMapping protoMapping, SpringReflectContent runApplication, SocketSession socketSession, int messageId, byte[] bytes) {
        super(protoMapping.method());
        this.protoMapping = protoMapping;
        this.runApplication = runApplication;
        this.socketSession = socketSession;
        this.messageId = messageId;
        pojoBase = ReflectContext.newInstance(protoMapping.pojoClass());
        pojoBase.decode(bytes);
    }

    @Override public void onEvent() throws Exception {
        try {
            protoMapping.method().invoke(protoMapping.ins(), injectorParameters(runApplication, socketSession, pojoBase));
        } catch (Throwable e) {
            log.error("{} messageId={}, {}", socketSession, messageId, protoMapping.pojoClass().getSimpleName(), e);
        }
    }

    public Object[] injectorParameters(SpringReflectContent springReflectContent, SocketSession socketSession, PojoBase pojoBase) {
        Parameter[] parameters = protoMapping.method().getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();
            Type parameterizedType = parameter.getParameterizedType();
            if (SpringReflectContent.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(springReflectContent);
                continue;
            } else if (ApplicationContext.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(springReflectContent.getApplicationContext());
                continue;
            } else if (SocketSession.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(socketSession);
                continue;
            } else if (pojoBase.getClass().isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(pojoBase);
                continue;
            }
            /*实现注入*/
            {
                Value value = parameter.getAnnotation(Value.class);
                if (value != null) {
                    params[i] = springReflectContent.configValue(value, parameterizedType);
                    continue;
                }
            }

            try {
                params[i] = springReflectContent.getApplicationContext().getBean(parameterType);
            } catch (Exception e) {
                Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
                // if (qualifier != null && qualifier.required()) {
                //     throw new RuntimeException("bean:" + parameterType.getName() + " is not bind");
                // }
            }
        }
        return params;
    }
}
