package wxdgaming.spring.boot.net;

/**
 * session 处理器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-07 17:43
 **/
public interface SessionHandler {

    default void openSession(SocketSession session) {}

    default void closeSession(SocketSession session) {}

    default void exception(SocketSession session, Throwable throwable) {}

}
