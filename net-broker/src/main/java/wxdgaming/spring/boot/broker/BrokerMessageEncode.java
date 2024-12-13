package wxdgaming.spring.boot.broker;

import io.netty.channel.ChannelHandler;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.net.server.ServerMessageDispatcher;
import wxdgaming.spring.boot.net.server.ServerMessageEncode;

/**
 * 代理解码
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-17 16:36
 **/
@ChannelHandler.Sharable
public class BrokerMessageEncode extends ServerMessageEncode implements InitPrint {

    public BrokerMessageEncode(ServerMessageDispatcher messageDispatcher) {
        super(messageDispatcher);
    }

}
