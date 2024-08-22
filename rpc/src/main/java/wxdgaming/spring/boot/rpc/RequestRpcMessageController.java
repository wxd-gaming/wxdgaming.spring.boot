package wxdgaming.spring.boot.rpc;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.net.MsgMapper;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.rpc.pojo.RpcMessage;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * 请求rpc执行处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-22 19:43
 **/
@Slf4j
@Controller
public class RequestRpcMessageController {

    private final RpcService rpcService;

    public RequestRpcMessageController(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    @MsgMapper
    public void rpcReqSocketAction(SocketSession session, RpcMessage.ReqRemote reqRemote) throws Exception {
        long rpcId = reqRemote.getRpcId();
        String path = reqRemote.getPath();
        String remoteParams = reqRemote.getParams();
        RpcActionMapping rpcActionMapping = rpcService.getRpcHandlerMap().get(path);
        if (rpcActionMapping == null) {
            log.error("rpc 调用异常 rpcId={}, path={}, params={}", rpcId, path, remoteParams);
            rpcService.response(session, rpcId, 9, "Not found path=【" + path + "】!");
            return;
        }

        if (!rpcService.getRPC_TOKEN().equalsIgnoreCase(reqRemote.getRpcToken())) {
            log.error("rpc 调用验签失败 rpcId={}, path={}, params={}", rpcId, path, remoteParams);
            rpcService.response(session, rpcId, 10, "token error path=【" + path + "】!");
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

        try {
            /* 调用 */
            Object invoke = rpcActionMapping.getMethod().invoke(rpcActionMapping.getBean(), params);
            if (rpcId < 1) {
                return;
            }
            RpcMessage.ResRemote res;
            if (invoke instanceof RpcMessage.ResRemote resRemote) {
                res = resRemote;
            } else {
                res = new RpcMessage.ResRemote();
                res.setCode(1);
                if (invoke != null) {
                    res.setParams(String.valueOf(invoke));
                }
            }
            res.setRpcId(rpcId);
            res.setRpcToken(rpcService.getRPC_TOKEN());
            session.writeAndFlush(res);
        } catch (Throwable t) {
            rpcService.response(session, rpcId, 500, t.getMessage());
        }
    }

}
