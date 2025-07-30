package wxdgaming.spring.boot.net.pojo;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import wxdgaming.spring.boot.core.ApplicationContextProvider;
import wxdgaming.spring.boot.core.ann.ThreadParam;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.executor.ExecutorEvent;
import wxdgaming.spring.boot.core.executor.ThreadContext;
import wxdgaming.spring.boot.core.reflect.ReflectProvider;
import wxdgaming.spring.boot.net.SocketSession;

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
public class ProtoListenerTrigger extends ExecutorEvent {

    private final ProtoMapping protoMapping;
    private final ApplicationContextProvider contextProvider;
    private final SocketSession socketSession;
    private final int messageId;
    private final byte[] bytes;
    private PojoBase pojoBase;

    public ProtoListenerTrigger(ProtoMapping protoMapping, ApplicationContextProvider contextProvider, SocketSession socketSession, int messageId, byte[] bytes) {
        this.protoMapping = protoMapping;
        this.contextProvider = contextProvider;
        this.socketSession = socketSession;
        this.messageId = messageId;
        this.bytes = bytes;
    }

    @Override public String getStack() {
        return "ProtoListenerTrigger %s messageId=%s, %s".formatted(socketSession, messageId, getPojoBase());
    }

    @Override public void onEvent() throws Exception {
        try {
            protoMapping.javassistProxy().proxyInvoke(injectorParameters());
        } catch (Throwable e) {
            log.error("{} messageId={}, {}", socketSession, messageId, getPojoBase(), e);
        }
    }

    public Object[] injectorParameters() {
        Parameter[] parameters = protoMapping.javassistProxy().getMethod().getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();
            Type parameterizedType = parameter.getParameterizedType();
            if (ApplicationContextProvider.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(contextProvider);
                continue;
            } else if (ApplicationContext.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(contextProvider.getApplicationContext());
                continue;
            } else if (SocketSession.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(socketSession);
                continue;
            } else if (protoMapping.pojoClass().isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(getPojoBase());
                continue;
            }
            /*实现注入*/
            {
                Value value = parameter.getAnnotation(Value.class);
                if (value != null) {
                    params[i] = contextProvider.configValue(value, parameterizedType);
                    continue;
                }
            }

            {
                ThreadParam threadParam = parameter.getAnnotation(ThreadParam.class);
                if (threadParam != null) {
                    params[i] = ThreadContext.context(threadParam, parameterizedType);
                    continue;
                }
            }
            /*实现注入*/
            Qualifier qualifier = parameter.getAnnotation(Qualifier.class);
            if (qualifier != null) {
                String name = qualifier.value();
                params[i] = contextProvider.getApplicationContext().getBean(name);
                continue;
            }
            params[i] = contextProvider.getApplicationContext().getBean(parameterType);
        }
        return params;
    }

    public PojoBase getPojoBase() {
        if (pojoBase == null) {
            pojoBase = ReflectProvider.newInstance(protoMapping.pojoClass());
            pojoBase.decode(bytes);
        }
        return pojoBase;
    }
}
