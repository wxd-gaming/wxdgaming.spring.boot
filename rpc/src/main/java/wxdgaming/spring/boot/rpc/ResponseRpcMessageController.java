package wxdgaming.spring.boot.rpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import wxdgaming.spring.boot.net.MsgMapper;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.rpc.pojo.RpcMessage;

import java.util.concurrent.CompletableFuture;

/**
 * 消息回调
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-22 19:46
 **/
@Slf4j
@Controller
public class ResponseRpcMessageController {

    private final RpcService rpcService;

    public ResponseRpcMessageController(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    @MsgMapper
    public void rpcResSocketAction(SocketSession session, RpcMessage.ResRemote resRemote) throws Exception {
        long rpcId = resRemote.getRpcId();
        String rpcToken = resRemote.getRpcToken();
        int code = resRemote.getCode();
        String remoteParams = resRemote.getParams();
        CompletableFuture<String> completableFuture = rpcService.getRpcEvent().remove(rpcId);
        if (code != 1) {
            log.error("rpc 调用异常 rpcId={}, code={}, msg={}", rpcId, code, remoteParams);
            completableFuture.completeExceptionally(new RuntimeException("code=" + code + ", msg=" + remoteParams));
            return;
        }
        log.debug("rpc 调用完成 rpcId={}, param={}", rpcId, remoteParams);
        completableFuture.complete(remoteParams);
    }

}
