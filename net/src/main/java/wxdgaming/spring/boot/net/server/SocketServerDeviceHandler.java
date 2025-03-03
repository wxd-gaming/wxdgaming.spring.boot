package wxdgaming.spring.boot.net.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import wxdgaming.spring.boot.net.BootstrapBuilder;
import wxdgaming.spring.boot.net.ChannelUtil;
import wxdgaming.spring.boot.net.SocketDeviceHandler;
import wxdgaming.spring.boot.net.SocketSession;

/**
 * socket server 驱动
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-16 09:05
 **/
@ChannelHandler.Sharable
public class SocketServerDeviceHandler extends SocketDeviceHandler {

    private final SocketService socketService;

    public SocketServerDeviceHandler(BootstrapBuilder bootstrapBuilder, SocketService socketService) {
        super(bootstrapBuilder);
        this.socketService = socketService;
    }

    @Override public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        /*TODO 构造函数自动注册*/
        SocketSession socketSession = new SocketSession(SocketSession.Type.server, ctx.channel(), ChannelUtil.attr(ctx.channel(), ChannelUtil.WEB_SOCKET_SESSION_KEY));
        socketSession.setMaxFrameBytes(socketService.getConfig().getMaxFrameBytes());
        socketSession.setMaxFrameLength(socketService.getConfig().getMaxFrameLength());
        socketService.getSessionGroup().add(socketSession);
        ctx.channel().closeFuture().addListener(future -> socketService.getSessionGroup().remove(socketSession));
        sessionHandler.openSession(socketSession);
    }

}
