package wxdgaming.spring.boot.rpc;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import wxdgaming.spring.boot.net.MsgMapper;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.rpc.pojo.RpcMessage;

/**
 * 请求rpc执行处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-22 19:43
 **/
@Slf4j
@Getter
@Setter
@Controller
public class RequestRpcMessageController {

    @Value("${socket.printLogger:false}")
    boolean printLogger = false;
    final RpcService rpcService;

    public RequestRpcMessageController(RpcService rpcService) {
        this.rpcService = rpcService;
    }

    @MsgMapper
    public void rpcReqSocketAction(SocketSession session, RpcMessage.ReqRPC reqRemote) throws Exception {
        long rpcId = reqRemote.getRpcId();
        long targetId = reqRemote.getTargetId();
        String remoteParams = reqRemote.getParams();
        String path = reqRemote.getPath();
        rpcService.getRpcDispatcher().rpcReqSocketAction(session, rpcId, targetId, path, remoteParams);
    }


}
