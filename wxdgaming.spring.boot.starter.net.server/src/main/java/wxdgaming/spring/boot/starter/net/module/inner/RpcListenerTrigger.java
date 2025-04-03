package wxdgaming.spring.boot.starter.net.module.inner;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import wxdgaming.spring.boot.starter.core.SpringReflectContent;
import wxdgaming.spring.boot.starter.core.Throw;
import wxdgaming.spring.boot.starter.core.ann.ThreadParam;
import wxdgaming.spring.boot.starter.core.json.FastJsonUtil;
import wxdgaming.spring.boot.starter.core.lang.RunResult;
import wxdgaming.spring.boot.starter.core.threading.Event;
import wxdgaming.spring.boot.starter.core.threading.ThreadContext;
import wxdgaming.spring.boot.starter.core.util.StringUtils;
import wxdgaming.spring.boot.starter.net.SocketSession;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * rpc 触发 事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 09:15
 **/
@Getter
@Slf4j
public class RpcListenerTrigger extends Event {

    private final RpcMapping rpcMapping;
    private final RpcService rpcService;
    private final SpringReflectContent springReflectContent;
    private final SocketSession socketSession;
    private final long rpcId;
    private final JSONObject paramObject;

    public RpcListenerTrigger(RpcMapping rpcMapping,
                              RpcService rpcService,
                              SpringReflectContent springReflectContent,
                              SocketSession socketSession,
                              long rpcId,
                              JSONObject paramObject) {
        super();
        this.rpcMapping = rpcMapping;
        this.rpcService = rpcService;
        this.springReflectContent = springReflectContent;
        this.socketSession = socketSession;
        this.rpcId = rpcId;
        this.paramObject = paramObject;
    }

    @Override public String getTaskInfoString() {
        return "RpcListenerTrigger: " + rpcMapping.path() + "; " + rpcMapping.ins().getClass().getName() + "." + rpcMapping.method().getName() + "()";
    }

    @Override public void onEvent() {
        try {
            Object invoke = rpcMapping.method().invoke(rpcMapping.ins(), injectorParameters(springReflectContent, socketSession, paramObject));
            if (rpcMapping.method().getReturnType() == void.class) {
                invoke = null;
            }
            if (rpcId > 0) {
                RunResult data = RunResult.ok();
                if (invoke != null) {
                    data.data(invoke);
                }
                rpcService.response(socketSession, rpcId, data);
            }
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = e.getCause();
            }
            log.error("dispatch error rpcId: {}, cmd: {}, paramData: {}", rpcId, rpcMapping.path(), paramObject, e);
            if (rpcId > 0) {
                rpcService.response(socketSession, rpcId, RunResult.error(500, "server error"));
            }
        }
    }

    public Object[] injectorParameters(SpringReflectContent springReflectContent, SocketSession socketSession, JSONObject paramObject) {
        Parameter[] parameters = rpcMapping.method().getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> parameterType = parameter.getType();
            Type parameterizedType = parameter.getParameterizedType();
            /*实现注入*/
            {
                Value value = parameter.getAnnotation(Value.class);
                if (value != null) {
                    params[i] = springReflectContent.configValue(value, parameterizedType);
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
                    String name = null;
                    Object o;
                    try {
                        if (StringUtils.isNotBlank(param.name())) {
                            name = param.name();
                            o = FastJsonUtil.getNestedValue(paramObject, param.name(), parameterizedType);
                        } else {
                            name = param.value();
                            o = paramObject.getObject(param.value(), parameterizedType);
                        }
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
            if (SpringReflectContent.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(springReflectContent);
                continue;
            } else if (ApplicationContext.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(springReflectContent.getApplicationContext());
                continue;
            } else if (SocketSession.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(socketSession);
                continue;
            } else if (JSONObject.class.isAssignableFrom(parameterType)) {
                params[i] = parameterType.cast(paramObject);
                continue;
            }
            try {
                params[i] = springReflectContent.getApplicationContext().getBean(parameterType);
            } catch (Exception e) {
                Nullable qualifier = parameter.getAnnotation(Nullable.class);
                if (qualifier == null) {
                    throw new RuntimeException("bean:" + parameterType.getName() + " is not bind");
                }
            }
        }
        return params;
    }

}
