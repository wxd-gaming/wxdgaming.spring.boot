package wxdgaming.spring.boot.net.server;

import wxdgaming.spring.boot.net.MessageAction;
import wxdgaming.spring.boot.net.MessageDispatcher;

/**
 * 消息处理器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-15 11:03
 **/
public abstract class ServerMessageAction extends MessageAction {

    public ServerMessageAction(MessageDispatcher dispatcher) {
        super(dispatcher);
    }

}

