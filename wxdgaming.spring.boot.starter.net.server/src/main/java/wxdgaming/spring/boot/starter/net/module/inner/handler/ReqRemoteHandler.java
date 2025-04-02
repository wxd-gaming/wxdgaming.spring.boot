package wxdgaming.spring.boot.starter.net.module.inner.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.starter.core.SpringReflect;
import wxdgaming.spring.boot.starter.core.ann.Init;
import wxdgaming.spring.boot.starter.core.json.FastJsonUtil;
import wxdgaming.spring.boot.starter.core.lang.RunResult;
import wxdgaming.spring.boot.starter.core.zip.GzipUtil;
import wxdgaming.spring.boot.starter.net.SocketSession;
import wxdgaming.spring.boot.starter.net.ann.ProtoRequest;
import wxdgaming.spring.boot.starter.net.module.inner.*;
import wxdgaming.spring.boot.starter.net.module.inner.message.ReqRemote;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class ReqRemoteHandler {

    final RpcService rpcService;
    final RpcListenerFactory rpcListenerFactory;
    List<RpcFilter> rpcFilterList;

    @Autowired
    public ReqRemoteHandler(RpcService rpcService, RpcListenerFactory rpcListenerFactory) {
        this.rpcService = rpcService;
        this.rpcListenerFactory = rpcListenerFactory;
    }

    @Init
    public void init(SpringReflect springReflect) {
        rpcFilterList = springReflect.getSpringReflectContent().classWithSuper(RpcFilter.class).toList();
    }

    @ProtoRequest
    public void reqRemote(SocketSession socketSession, ReqRemote req) {
        long rpcId = req.getUid();
        String cmd = req.getCmd();
        String token = req.getToken();
        int gzip = req.getGzip();
        String params = req.getParams();
        if (gzip == 1) {
            params = GzipUtil.unGzip2String(params);
        }
        if (!cmd.startsWith("/")) cmd = "/" + cmd;
        JSONObject paramObject = FastJsonUtil.parse(params);

        try {
            String lowerCase = cmd.toLowerCase();
            RpcMapping rpcMapping = rpcListenerFactory.getRpcListenerContent().getRpcMappingMap().get(lowerCase);
            if (rpcMapping == null) {
                if (log.isDebugEnabled())
                    log.debug("rpcId: {}, cmd: {}, params: {} not cmd path", rpcId, cmd, params);
                if (rpcId > 0) {
                    rpcService.response(socketSession, rpcId, RunResult.error(9, "not cmd path"));
                }
                return;
            }
            if (log.isDebugEnabled())
                log.debug("rpcId: {}, cmd: {}, params: {}", rpcId, cmd, params);

            RpcListenerTrigger rpcListenerTrigger = new RpcListenerTrigger(
                    rpcMapping,
                    rpcService,
                    rpcListenerFactory.getRpcListenerContent().getRunApplication().getSpringReflectContent(),
                    socketSession,
                    rpcId,
                    paramObject
            );

            boolean allMatch = rpcFilterList.stream()
                    .allMatch(filter -> filter.doFilter(rpcListenerTrigger, lowerCase, socketSession, paramObject));
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
                rpcService.response(socketSession, rpcId, RunResult.error(500, "server error"));
            }
        }

    }

}