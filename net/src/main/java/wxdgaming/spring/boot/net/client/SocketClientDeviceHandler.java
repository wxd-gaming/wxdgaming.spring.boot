package wxdgaming.spring.boot.net.client;

import io.netty.channel.ChannelHandler;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.net.SocketDeviceHandler;

/**
 * socket server 驱动
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-16 09:05
 **/
@Slf4j
@ChannelHandler.Sharable
public class SocketClientDeviceHandler extends SocketDeviceHandler<ClientMessageAction> {

    public SocketClientDeviceHandler(ClientMessageAction messageAction, boolean autoRelease) {
        super(messageAction, autoRelease);
    }

}
