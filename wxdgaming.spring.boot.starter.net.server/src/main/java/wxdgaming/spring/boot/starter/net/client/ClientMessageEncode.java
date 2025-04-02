package wxdgaming.spring.boot.starter.net.client;

import io.netty.channel.Channel;
import wxdgaming.spring.boot.starter.net.MessageEncode;
import wxdgaming.spring.boot.starter.net.pojo.ProtoListenerFactory;

/**
 * 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-17 15:08
 **/
public class ClientMessageEncode extends MessageEncode {

    public ClientMessageEncode(boolean scheduledFlush, long scheduledDelayMs, Channel channel, ProtoListenerFactory protoListenerFactory) {
        super(scheduledFlush, scheduledDelayMs, channel, protoListenerFactory);
    }

}
