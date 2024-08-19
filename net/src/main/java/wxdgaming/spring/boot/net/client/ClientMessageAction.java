package wxdgaming.spring.boot.net.client;

import wxdgaming.spring.boot.net.MessageAction;
import wxdgaming.spring.boot.net.MessageDispatcher;

/**
 * 消息处理器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-15 11:03
 **/
public abstract class ClientMessageAction extends MessageAction {

    public ClientMessageAction(MessageDispatcher dispatcher) {
        super(dispatcher);
    }
}
