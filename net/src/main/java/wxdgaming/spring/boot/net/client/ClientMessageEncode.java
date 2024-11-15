package wxdgaming.spring.boot.net.client;

import io.netty.channel.ChannelHandler;
import wxdgaming.spring.boot.net.MessageDispatcher;
import wxdgaming.spring.boot.net.MessageEncode;

/**
 * 消息编码器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-15 11:03
 **/
@ChannelHandler.Sharable
public class ClientMessageEncode extends MessageEncode {

    public ClientMessageEncode(MessageDispatcher messageDispatcher) {
        super(messageDispatcher);
    }

}

