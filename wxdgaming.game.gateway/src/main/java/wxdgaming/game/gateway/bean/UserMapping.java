package wxdgaming.game.gateway.bean;

import io.netty.channel.ChannelFuture;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.lang.ObjectBase;
import wxdgaming.spring.boot.net.MessageEncode;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.pojo.PojoBase;

/**
 * 用户映射关系
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-28 17:03
 **/
@Getter
@Setter
@Accessors(chain = true)
public class UserMapping extends ObjectBase {

    private String account;
    private long chooseRoleId;
    private int crossServerId;
    private int gameServerId;
    private SocketSession clientSocketSession;

    public ChannelFuture send2Client(PojoBase pojoBase) {
        return clientSocketSession.write(pojoBase);
    }

    public ChannelFuture send2Client(int messageId, byte[] messages) {
        Object build = MessageEncode.build(getClientSocketSession(), messageId, messages);
        return getClientSocketSession().write(build);
    }

    public boolean isCrossing() {
        return crossServerId > 0;
    }

    public int gameServerId() {
        if (crossServerId > 0) {
            return crossServerId;
        }
        return gameServerId;
    }

    public long clientSessionId() {
        if (getClientSocketSession() == null) return 0;
        return getClientSocketSession().getUid();
    }

    @Override public String toString() {
        return "UserMapping{account='%s', chooseRoleId=%d, chooseServerId=%d, clientSessionId=%d}"
                .formatted(account, chooseRoleId, gameServerId, clientSessionId());
    }
}
