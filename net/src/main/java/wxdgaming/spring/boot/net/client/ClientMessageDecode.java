package wxdgaming.spring.boot.net.client;

import io.netty.channel.ChannelHandler;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.net.MessageDecode;
import wxdgaming.spring.boot.net.MessageDispatcher;

/**
 * 消息处理器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-15 11:03
 **/
@ChannelHandler.Sharable
public class ClientMessageDecode extends MessageDecode {

    public ClientMessageDecode(MessageDispatcher dispatcher) {
        super(true, dispatcher);
    }

}
