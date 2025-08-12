package wxdgaming.game.server.script.inner.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.inner.InnerForwardMessage;
import wxdgaming.game.server.bean.ClientSessionMapping;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.ClientSessionService;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.inner.InnerService;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.executor.ThreadContext;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;
import wxdgaming.spring.boot.net.pojo.ProtoListenerFactory;

import java.util.List;

/**
 * 请求转发消息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class InnerForwardMessageHandler extends HoldRunApplication {

    private final InnerService innerService;
    private final ClientSessionService clientSessionService;
    private final ProtoListenerFactory protoListenerFactory;
    private final DataCenterService dataCenterService;

    public InnerForwardMessageHandler(InnerService innerService,
                                      ClientSessionService clientSessionService,
                                      ProtoListenerFactory protoListenerFactory,
                                      DataCenterService dataCenterService) {
        this.innerService = innerService;
        this.clientSessionService = clientSessionService;
        this.protoListenerFactory = protoListenerFactory;
        this.dataCenterService = dataCenterService;
    }

    /** 请求转发消息 */
    @ProtoRequest
    public void innerForwardMessage(SocketSession socketSession, InnerForwardMessage req) {
        List<Long> sessionIds = req.getSessionIds();
        int messageId = req.getMessageId();
        byte[] messages = req.getMessages();


        ThreadContext.cleanup();
        ThreadContext.putContent("forwardMessage", req);
        String clientIp = req.getKvBeansMap().get("clientIp");
        ThreadContext.putContent("clientIp", Long.parseLong(clientIp));
        String clientSessionId = req.getKvBeansMap().get("clientSessionId");
        ThreadContext.putContent("clientSessionId", Long.parseLong(clientSessionId));
        String account = req.getKvBeansMap().get("account");
        if (account != null) {
            ClientSessionMapping clientSessionMapping = clientSessionService.getAccountMappingMap().get(account);
            ThreadContext.putContent("clientSessionMapping", clientSessionMapping);
            long rid = clientSessionMapping.getRid();
            Player player = dataCenterService.getPlayer(rid);
            ThreadContext.putContent("player", player);
        }
        protoListenerFactory.dispatch(socketSession, messageId, messages);
    }

}