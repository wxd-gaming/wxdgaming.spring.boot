package wxdgaming.spring.boot.rpc;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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

    final RpcService rpcService;

    public RequestRpcMessageController(RpcService rpcService) {this.rpcService = rpcService;}

    @MsgMapper
    public void rpcReqSocketAction(SocketSession session, RpcMessage.ReqRemote reqRemote) throws Exception {
        String rpcToken = reqRemote.getRpcToken();
        long rpcId = reqRemote.getRpcId();
        long targetId = reqRemote.getTargetId();
        String remoteParams = reqRemote.getParams();
        String path = reqRemote.getPath();
        try {
            Object invoke = rpcService.getRpcDispatcher().rpcReqSocketAction(session, rpcToken, rpcId, targetId, path, remoteParams);

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
            res.setRpcToken(rpcService.getRpcDispatcher().getRPC_TOKEN());
            session.writeAndFlush(res);

        } catch (Throwable t) {
            log.error("{}, rpcId={}, path={}, params={}", session, rpcId, path, remoteParams, t);
            rpcService.getRpcDispatcher().response(session, rpcId, targetId, 500, t.getMessage());
        }
    }


}
