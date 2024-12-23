package wxdgaming.spring.boot.rpc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${socket.printLogger:false}")
    boolean printLogger = false;

    final RpcService rpcService;

    public ResponseRpcMessageController(RpcService rpcService) {this.rpcService = rpcService;}

    @MsgMapper
    public void rpcResSocketAction(SocketSession session, RpcMessage.ResRPC resRemote) throws Exception {
        long rpcId = resRemote.getRpcId();
        long targetId = resRemote.getTargetId();
        int code = resRemote.getCode();
        String remoteParams = resRemote.getParams();
        CompletableFuture<String> completableFuture = rpcService.getRpcDispatcher().getRpcEvent().remove(rpcId);
        if (code != 1) {
            if (printLogger) {
                log.error("rpc 调用异常 rpcId={}, targetId={}, code={}, msg={}", rpcId, targetId, code, remoteParams);
            }
            completableFuture.completeExceptionally(new RuntimeException("code=" + code + ", targetId=" + targetId + ", msg=" + remoteParams));
            return;
        }
        if (printLogger) {
            log.info("rpc 调用完成 rpcId={}, targetId={}, param={}", rpcId, targetId, remoteParams);
        }
        completableFuture.complete(remoteParams);
    }

}
