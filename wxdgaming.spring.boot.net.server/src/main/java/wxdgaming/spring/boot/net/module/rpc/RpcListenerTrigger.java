package wxdgaming.spring.boot.net.module.rpc;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import wxdgaming.spring.boot.core.ApplicationContextProvider;
import wxdgaming.spring.boot.core.Throw;
import wxdgaming.spring.boot.core.ann.ThreadParam;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.executor.ExecutorEvent;
import wxdgaming.spring.boot.core.executor.ThreadContext;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.boot.net.SocketSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * rpc 触发 事件
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-18 09:15
 **/
@Slf4j
public class RpcListenerTrigger extends ExecutorEvent {

    private final RpcMapping rpcMapping;
    private final RpcService rpcService;
    private final ApplicationContextProvider contextProvider;
    private final SocketSession socketSession;
    private final long rpcId;
    private final JSONObject paramObject;

    public RpcListenerTrigger(RpcMapping rpcMapping,
                              RpcService rpcService,
                              ApplicationContextProvider contextProvider,
                              SocketSession socketSession,
                              long rpcId,
                              JSONObject paramObject) {
        this.rpcMapping = rpcMapping;
        this.rpcService = rpcService;
        this.contextProvider = contextProvider;
        this.socketSession = socketSession;
        this.rpcId = rpcId;
        this.paramObject = paramObject;
    }

    @Override public String getStack() {
        return "RpcListenerTrigger: %s; %s.%s()".formatted(
                rpcMapping.path(),
                rpcMapping.javassistProxy().getInstance().getClass().getName(),
                rpcMapping.javassistProxy().getMethod().getName()
        );
    }

    @Override public void onEvent() {
        try {
            Object invoke = rpcMapping.javassistProxy().proxyInvoke(injectorParameters());
            if (rpcMapping.javassistProxy().getMethod().getReturnType() == void.class) {
                invoke = null;
            }
            if (rpcId > 0) {
                RunResult ret;
                if (invoke instanceof RunResult runResult) {
                    ret = runResult;
                } else {
                    ret = RunResult.ok();
                    if (invoke != null) {
                        ret.data(invoke);
                    }
                }
                rpcService.response(socketSession, rpcId, ret);
            }
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = e.getCause();
            }
            log.error("dispatch error rpcId: {}, cmd: {}, paramData: {}", rpcId, rpcMapping.path(), paramObject, e);
            if (rpcId > 0) {
                rpcService.response(socketSession, rpcId, RunResult.fail(500, "server error"));
            }
        }
    }

    public Object[] injectorParameters() {
        Parameter[] parameters = rpcMapping.javassistProxy().getMethod().getParameters();
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
            } else if (JSONObject.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(paramObject);
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
            {
                RequestParam param = parameter.getAnnotation(RequestParam.class);
                if (param != null) {
                    String name = StringUtils.isBlank(param.name()) ? param.value() : param.name();
                    Object o;
                    try {
                        o = paramObject.getObject(name, parameterizedType);
                        if (o == null && StringUtils.isNotBlank(param.defaultValue())) {
                            o = FastJsonUtil.parse(param.defaultValue(), parameterizedType);
                        }
                    } catch (Exception e) {
                        throw Throw.of("param 参数：" + name, e);
                    }
                    if (param.required() && o == null) {
                        throw new RuntimeException("param:" + name + " is null");
                    }
                    params[i] = o;
                    continue;
                }
            }

            {
                RequestBody body = parameter.getAnnotation(RequestBody.class);
                if (body != null) {
                    Object o = null;
                    if (!paramObject.isEmpty()) {
                        o = paramObject.toJavaObject(parameterType);
                    }
                    if (body.required() && o == null) {
                        throw new RuntimeException("body is null");
                    }
                    params[i] = o;
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

}
