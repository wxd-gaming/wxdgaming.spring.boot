package wxdgaming.spring.boot.net.client;

import wxdgaming.spring.boot.net.SocketSession;

/**
 * 实现监听
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 12:14
 **/
public interface IClientWebSocketStringListener {

    void onMessage(SocketSession socketSession, String message);

}
