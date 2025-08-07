package wxdgaming.spring.boot.net.pojo;

import wxdgaming.spring.boot.net.SocketSession;

/**
 * proto 未知消息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-28 20:24
 **/
public interface ProtoUnknownMessageEvent {

    void onUnknownMessageEvent(SocketSession socketSession, int messageId, byte[] messages);

}
