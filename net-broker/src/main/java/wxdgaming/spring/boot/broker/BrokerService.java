package wxdgaming.spring.boot.broker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.broker.pojo.inner.InnerMessage;
import wxdgaming.spring.boot.core.collection.concurrent.ConcurrentTable;
import wxdgaming.spring.boot.net.BootstrapBuilder;
import wxdgaming.spring.boot.net.SessionGroup;
import wxdgaming.spring.boot.net.SessionHandler;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.server.ServerMessageEncode;
import wxdgaming.spring.boot.net.server.SocketServerBuilder;
import wxdgaming.spring.boot.net.server.SocketService;

/**
 * 转发服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-17 16:31
 */
@Slf4j
@Getter
public class BrokerService extends SocketService {

    private ConcurrentTable<InnerMessage.Stype, Integer, SocketSession> sessions = new ConcurrentTable<>();

    public BrokerService(BootstrapBuilder bootstrapBuilder,
                         SocketServerBuilder socketServerBuilder,
                         SocketServerBuilder.Config config,
                         SessionHandler sessionHandler,
                         SessionGroup sessionGroup,
                         BrokerMessageDecode brokerMessageDecode,
                         ServerMessageEncode serverMessageEncode) {

        super(
                bootstrapBuilder,
                socketServerBuilder,
                config,
                sessionHandler,
                sessionGroup,
                brokerMessageDecode,
                serverMessageEncode
        );

    }


}
