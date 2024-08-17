package wxdgaming.spring.boot.net.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.net.ChannelUtil;
import wxdgaming.spring.boot.net.MessageAction;
import wxdgaming.spring.boot.net.SocketDeviceHandler;
import wxdgaming.spring.boot.net.SocketSession;

/**
 * socket server 驱动
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-16 09:05
 **/
@Slf4j
@ChannelHandler.Sharable
public class SocketServerDeviceHandler extends SocketDeviceHandler<ServerMessageAction> {

    public SocketServerDeviceHandler(ServerMessageAction messageAction, boolean autoRelease) {
        super(messageAction, autoRelease);
    }

    @Override public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        /*TODO 构造函数自动注册*/
        new SocketSession(ctx.channel(), ChannelUtil.attr(ctx.channel(), ChannelUtil.WEB_SOCKET_SESSION_KEY));
    }



}
