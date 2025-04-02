package wxdgaming.spring.boot.starter.net.server;

import wxdgaming.spring.boot.starter.net.SocketSession;

/**
 * socket事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-02 11:23
 **/
public interface SocketServerEvent {

    default void onOpen(SocketServer socketServer, SocketSession socketSession) {}

    default void onClose(SocketServer socketServer, SocketSession socketSession) {}

    default void onException(SocketSession socketSession, Throwable throwable) {}

}
