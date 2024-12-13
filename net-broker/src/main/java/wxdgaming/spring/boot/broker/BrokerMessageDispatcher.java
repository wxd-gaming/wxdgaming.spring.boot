package wxdgaming.spring.boot.broker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.net.server.ServerMessageDispatcher;

/**
 * 消息派发服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-17 11:35
 **/
@Slf4j
@Getter
public class BrokerMessageDispatcher extends ServerMessageDispatcher implements InitPrint {

    public BrokerMessageDispatcher(String[] packages) {
        super(packages);
    }

}
