package wxdgaming.spring.boot.net;

import io.netty.buffer.ByteBuf;
import wxdgaming.spring.boot.core.collection.concurrent.ConcurrentLoopList;
import wxdgaming.spring.boot.message.PojoBase;

/**
 * session
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-07 17:42
 */
public interface ISession {

    ConcurrentLoopList<SocketSession> getSessions();

    default SocketSession idleSession() {
        return getSessions().loop();
    }

    /** 循环获取 如果 null 则会引发异常 */
    default SocketSession idleSessionOrException() {
        return getSessions().loopOrException();
    }

    default void writeAndFlush(PojoBase pojoBase) {
        getSessions().forEach(session -> {session.writeAndFlush(pojoBase);});
    }

    default void writeAndFlush(ByteBuf byteBuf) {
        getSessions().forEach(session -> {session.writeAndFlush(byteBuf);});
    }

    default void writeAndFlush(Object message) {
        getSessions().forEach(session -> {session.writeAndFlush(message);});
    }


}
