package wxdgaming.game.server.bean;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.lang.ObjectBase;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.pojo.PojoBase;
import wxdgaming.game.message.inner.InnerForwardMessage;
import wxdgaming.game.server.bean.role.Player;

import java.util.function.Consumer;

/**
 * 客户端session映射
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 20:41
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class ClientSessionMapping extends ObjectBase {

    /** 客户端session */
    private SocketSession session;
    private int gatewayId;
    private long clientSessionId;
    private int sid;
    private String account;
    private int appId;
    private String platform;
    private String platformUserId;
    private long rid;

    public void forwardMessage(Player player, PojoBase message) {
        forwardMessage(player, message, null);
    }

    public void forwardMessage(Player player, PojoBase message, Consumer<InnerForwardMessage> callback) {
        InnerForwardMessage msg = new InnerForwardMessage();
        msg.getSessionIds().add(clientSessionId);
        msg.setMessageId(message.msgId());
        msg.setMessages(message.encode());
        if (callback != null) {
            callback.accept(msg);
        }
        if (log.isDebugEnabled()) {
            log.debug("发送消息：clientSessionId={},player={}, msgId={}, {}", clientSessionId, player, message.msgId(), message);
        }
        session.write(msg);
    }

    @Override public String toString() {
        return "ClientSessionMapping{clientSessionId=%d, sid=%d, account='%s', rid=%d, session=%s}"
                .formatted(clientSessionId, sid, account, rid, session);
    }
}
