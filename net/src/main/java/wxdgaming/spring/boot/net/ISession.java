package wxdgaming.spring.boot.net;

import io.netty.buffer.ByteBuf;
import wxdgaming.spring.boot.core.collection.concurrent.ConcurrentLoopList;
import wxdgaming.spring.boot.net.message.PojoBase;

/**
 * session
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-07 17:42
 */
public interface ISession {

    ConcurrentLoopList<SocketSession> getSessionGroup();

    default SocketSession idleSession() {
        return getSessionGroup().loop();
    }

    /** 循环获取 如果 null 则会引发异常 */
    default SocketSession idleSessionOrException() {
        return getSessionGroup().loopOrException();
    }

    default void writeAndFlush(PojoBase pojoBase) {
        getSessionGroup().forEach(session -> {session.writeAndFlush(pojoBase);});
    }

    default void writeAndFlush(ByteBuf byteBuf) {
        getSessionGroup().forEach(session -> {session.writeAndFlush(byteBuf);});
    }

    default void writeAndFlush(Object message) {
        getSessionGroup().forEach(session -> {session.writeAndFlush(message);});
    }


}
