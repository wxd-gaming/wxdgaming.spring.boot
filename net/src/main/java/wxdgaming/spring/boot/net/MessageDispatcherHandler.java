package wxdgaming.spring.boot.net;

import ch.qos.logback.core.LogbackUtil;
import org.slf4j.Logger;
import wxdgaming.spring.boot.net.message.PojoBase;

/**
 * 消息派发服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-17 11:35
 **/
public abstract class MessageDispatcherHandler {
    private boolean printLogger = false;

    public MessageDispatcherHandler(boolean printLogger) {
        this.printLogger = printLogger;
    }

    public void msgBytesNotDispatcher(SocketSession session, int msgId, byte[] messageBytes) throws Exception {
        if (printLogger) {
            Logger logger = LogbackUtil.logger();
            logger.debug(
                    "收到消息：ctx={}, id={}, len={} ({})",
                    session.toString(),
                    msgId,
                    messageBytes.length,
                    "未知消息"
            );
        }
    }

    public void msgNotDispatcher(SocketSession session, int msgId, PojoBase message) {
        if (printLogger) {
            Logger logger = LogbackUtil.logger();
            logger.debug(
                    "收到消息：ctx={}, id={}, mes={}",
                    session.toString(),
                    msgId,
                    message.toString()
            );
        }
    }

    public void stringDispatcher(SocketSession session, String message) {
        if (printLogger) {
            Logger logger = LogbackUtil.logger();
            logger.debug(
                    "收到消息：ctx={}, mes={}",
                    session.toString(),
                    message
            );
        }
    }


}
