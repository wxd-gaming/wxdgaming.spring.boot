package wxdgaming.spring.boot.starter.net.module.inner.handler;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.starter.core.json.FastJsonUtil;
import wxdgaming.spring.boot.starter.core.zip.GzipUtil;
import wxdgaming.spring.boot.starter.net.SocketSession;
import wxdgaming.spring.boot.starter.net.ann.ProtoRequest;
import wxdgaming.spring.boot.starter.net.module.inner.RpcService;
import wxdgaming.spring.boot.starter.net.module.inner.message.ResRemote;

import java.util.concurrent.CompletableFuture;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Service
public class ResRemoteHandler {

    final RpcService rpcService;

    @Autowired
    public ResRemoteHandler(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    @ProtoRequest
    public void resRemote(SocketSession socketSession, ResRemote req) {
        long rpcId = req.getUid();
        String token = req.getToken();
        String params = req.getParams();
        if (req.getGzip() == 1) {
            params = GzipUtil.unGzip2String(params);
        }
        JSONObject jsonObject = FastJsonUtil.parse(params);
        int code = jsonObject.getIntValue("code");
        CompletableFuture<JSONObject> stringCompletableFuture = rpcService.responseFuture(rpcId);
        if (code == 1) {
            stringCompletableFuture.complete(jsonObject);
        } else {
            RuntimeException ex = new RuntimeException(params);
            ex.setStackTrace(new StackTraceElement[0]);
            stringCompletableFuture.completeExceptionally(ex);
        }
    }

}