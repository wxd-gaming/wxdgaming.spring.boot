package wxdgaming.spring.boot.rpc;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.net.MsgMapper;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.rpc.pojo.RpcMessage;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-16 23:25
 **/
@Slf4j
@Service
public class RpcDispatcher implements InitPrint {

    private final SpringUtil springUtil;
    private final ConcurrentHashMap<String, RpcActionMapping> rpcHandlerMap = new ConcurrentHashMap<>();

    @Autowired
    public RpcDispatcher(SpringUtil springUtil) {
        this.springUtil = springUtil;
    }

    @Start
    @Order(99999)
    public void start() {
        springUtil.withMethodAnnotated(RPC.class)
                .forEach(method -> {
                    method.setAccessible(true);
                    Object bean = springUtil.getBean(method.getDeclaringClass());
                    String value = method.getAnnotation(RPC.class).value();
                    if (StringsUtil.emptyOrNull(value)) {
                        value = method.getName();
                    }
                    if (rpcHandlerMap.putIfAbsent(value, new RpcActionMapping(method, bean)) != null) {

                    }
                });
    }

    @MsgMapper
    public void rpcReqSocketAction(SocketSession session, RpcMessage.ReqRemote reqRemote) throws Exception {
        long rpcId = reqRemote.getRpcId();
        String rpcToken = reqRemote.getRpcToken();
        String path = reqRemote.getPath();
        String remoteParams = reqRemote.getParams();
        RpcActionMapping rpcActionMapping = rpcHandlerMap.get(path);
        if (rpcActionMapping == null) {
            log.error("rpc 调用异常 rpcId={}, path={}, params={}", rpcId, path, remoteParams);
            RpcMessage.ResRemote resRemote = new RpcMessage.ResRemote();
            resRemote.setRpcId(rpcId);
            resRemote.setRpcToken(rpcToken);
            resRemote.setCode(9);
            resRemote.setParams("Not found path!");
            session.writeAndFlush(resRemote);
            return;
        }

        Parameter[] parameters = rpcActionMapping.getMethod().getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Type type = parameter.getParameterizedType();
            if (type instanceof Class<?> clazz) {
                if (clazz.isAssignableFrom(session.getClass())) {
                    params[i] = session;
                } else if (clazz.getName().equals(JSONObject.class.getName())) {
                    params[i] = FastJsonUtil.parse(remoteParams);
                } else {
                    /*实现注入*/
                    RequestParam annotation = parameter.getAnnotation(RequestParam.class);
                    params[i] = FastJsonUtil.parse(remoteParams).getObject(annotation.name(), clazz);
                }
            }
        }

        RpcMessage.ResRemote res;
        try {
            Object invoke = rpcActionMapping.getMethod().invoke(rpcActionMapping.getBean(), params);
            if (rpcId < 1) {
                return;
            }
            if (invoke instanceof RpcMessage.ResRemote resRemote) {
                res = resRemote;
            } else {
                res = new RpcMessage.ResRemote();
                res.setCode(1);
                if (invoke != null) {
                    res.setParams(String.valueOf(invoke));
                }
            }
        } catch (Throwable t) {
            res = new RpcMessage.ResRemote();
            res.setCode(500);
            res.setParams(String.valueOf(t));
        }
        res.setRpcId(rpcId);
        res.setRpcToken(rpcToken);
        session.writeAndFlush(res);
    }

    @MsgMapper
    public void rpcResSocketAction(SocketSession session, RpcMessage.ResRemote resRemote) throws Exception {
        long rpcId = resRemote.getRpcId();
        String rpcToken = resRemote.getRpcToken();
        int code = resRemote.getCode();
        String remoteParams = resRemote.getParams();
        if (code != 1) {
            log.error("rpc 调用异常 rpcId={}, code={}, msg={}", rpcId, code, remoteParams);
            return;
        }
        log.debug("rpc 调用完成 rpcId={}, param={}", rpcId, remoteParams);
    }

    /** rpc test */
    @RPC
    public String rpcTest(SocketSession session, JSONObject jsonObject, @RequestParam(name = "type") int type) throws Exception {
        log.debug("rpc action rpcTest {}, {}, {}", session, jsonObject, type);
        return "ok";
    }

}
