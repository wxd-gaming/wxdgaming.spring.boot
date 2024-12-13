package wxdgaming.spring.boot.net;

import org.slf4j.Logger;
import wxdgaming.spring.boot.core.LogbackUtil;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-13 16:25
 **/
public interface DoMessage {

    /** 当找不到处理接口的时候调用的 */
    default void notSpi(SocketSession socketSession, int messageId, byte[] messageBytes) {
        Logger logger = LogbackUtil.logger();
        if (logger.isDebugEnabled()) {
            logger.debug(
                    "收到消息：ctx={}, id={}, len={} (未知消息)",
                    socketSession.toString(),
                    messageId,
                    messageBytes.length
            );
        }
    }

    default void actionString(SocketSession socketSession, String message) throws Exception {
        Logger logger = LogbackUtil.logger();
        if (logger.isDebugEnabled()) {
            logger.debug("收到消息：ctx={}, message={}", socketSession.toString(), message);
        }
    }

}
