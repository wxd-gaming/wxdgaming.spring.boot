package wxdgaming.spring.boot.net.client;

import wxdgaming.spring.boot.net.MessageDispatcher;

/**
 * 服务需要的接口派发
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-12 16:00
 **/
public class ClientMessageDispatcher extends MessageDispatcher {

    public ClientMessageDispatcher(boolean printLogger, String[] packages) {
        super(printLogger, packages);
    }

}
