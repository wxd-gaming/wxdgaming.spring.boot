package wxdgaming.spring.boot.net.module.inner.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.executor.ThreadContext;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.boot.core.zip.GzipUtil;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;
import wxdgaming.spring.boot.net.module.inner.*;
import wxdgaming.spring.boot.net.module.inner.message.ReqRemote;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqRemoteHandler {

    final RpcService rpcService;
    final RpcListenerFactory rpcListenerFactory;

    @Autowired
    public ReqRemoteHandler(RpcService rpcService, RpcListenerFactory rpcListenerFactory) {
        this.rpcService = rpcService;
        this.rpcListenerFactory = rpcListenerFactory;
    }

    @ProtoRequest(ignoreQueue = true)
    public void reqRemote(SocketSession socketSession, ReqRemote req) {
        long rpcId = req.getUid();
        String cmd = req.getCmd();
        String token = req.getToken();
        if (!Objects.equals(rpcService.sign(rpcId), token)) {
            log.error("rpc ({}-{}) 调用异常 token 无效 ", rpcId, cmd);
            return;
        }
        int gzip = req.getGzip();
        String params = req.getParams();
        if (gzip == 1) {
            params = GzipUtil.unGzip2String(params);
        }
        if (!cmd.startsWith("/")) cmd = "/" + cmd;
        log.debug("rpcId: {}, cmd: {}, params: {}", rpcId, cmd, params);
        JSONObject paramObject = FastJsonUtil.parse(params);

        try {
            String lowerCase = cmd.toLowerCase();
            RpcListenerContent rpcListenerContent = rpcListenerFactory.getRpcListenerContent();
            RpcMapping rpcMapping = rpcListenerContent.getRpcMappingMap().get(lowerCase);
            if (rpcMapping == null) {
                if (rpcId > 0) {
                    rpcService.response(socketSession, rpcId, RunResult.fail(9, "not cmd path"));
                }
                return;
            }

            ThreadContext.cleanup();
            RpcListenerTrigger rpcListenerTrigger = new RpcListenerTrigger(
                    rpcMapping,
                    rpcService,
                    rpcListenerContent.getContextProvider(),
                    socketSession,
                    rpcId,
                    paramObject
            );

            boolean allMatch = rpcListenerContent.getRpcFilterList().stream()
                    .allMatch(filter -> filter.doFilter(rpcListenerTrigger, lowerCase));
            if (!allMatch) {
                return;
            }

            rpcListenerTrigger.submit();
        } catch (Throwable e) {
            if (e instanceof InvocationTargetException) {
                e = e.getCause();
            }
            log.error("dispatch error rpcId: {}, cmd: {}, paramData: {}", rpcId, cmd, params, e);
            if (rpcId > 0) {
                rpcService.response(socketSession, rpcId, RunResult.fail(500, "server error"));
            }
        }

    }

}