package wxdgaming.spring.boot.net.server;

import io.netty.channel.ChannelHandler;
import wxdgaming.spring.boot.net.BootstrapBuilder;
import wxdgaming.spring.boot.net.MessageDecode;

/**
 * 消息解码器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-15 11:03
 **/
@ChannelHandler.Sharable
public class ServerMessageDecode extends MessageDecode {

    public ServerMessageDecode(ServerMessageDispatcher serverMessageDispatcher) {
        super(false, serverMessageDispatcher);
    }
}

