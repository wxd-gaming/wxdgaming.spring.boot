package wxdgaming.game.gateway.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.gateway.bean.ServerMapping;
import wxdgaming.game.gateway.module.data.DataCenterService;
import wxdgaming.game.message.role.ReqLogin;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;
import wxdgaming.spring.boot.net.pojo.ProtoListenerFactory;

/**
 * 登录请求
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class ReqLoginHandler {

    private final DataCenterService dataCenterService;
    private final ProtoListenerFactory protoListenerFactory;

    public ReqLoginHandler(DataCenterService dataCenterService, ProtoListenerFactory protoListenerFactory) {
        this.dataCenterService = dataCenterService;
        this.protoListenerFactory = protoListenerFactory;
    }


    /** 登录请求 */
    @ProtoRequest
    public void reqLogin(SocketSession socketSession, ReqLogin req) {
        int sid = req.getSid();
        ServerMapping serverMapping = dataCenterService.getGameServiceMappings().get(sid);
        if (serverMapping == null) {
            log.error("sid:{} 不存在", sid);
            socketSession.close("异常消息");
            return;
        }
        socketSession.bindData("gameServerId", sid);
        serverMapping.forwardMessage(socketSession.getUid(), req.msgId(), req.encode());

    }

}