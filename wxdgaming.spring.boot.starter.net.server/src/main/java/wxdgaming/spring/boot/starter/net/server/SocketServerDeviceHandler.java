package wxdgaming.spring.boot.starter.net.server;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.starter.core.SpringReflect;
import wxdgaming.spring.boot.starter.core.system.BytesUnit;
import wxdgaming.spring.boot.starter.net.ChannelUtil;
import wxdgaming.spring.boot.starter.net.SocketDeviceHandler;
import wxdgaming.spring.boot.starter.net.SocketSession;

import java.util.List;

/**
 * socket server 驱动
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-16 09:05
 **/
@Slf4j
@ChannelHandler.Sharable
public class SocketServerDeviceHandler extends SocketDeviceHandler {

    SocketServer socketServer;
    SpringReflect springReflect;
    List<SocketServerEvent> list;
    final SocketServerConfig socketServerConfig;

    public SocketServerDeviceHandler(SocketServerConfig socketServerConfig) {
        this.socketServerConfig = socketServerConfig;
    }

    protected void reset(SocketServer socketServer, SpringReflect springReflect) {
        this.socketServer = socketServer;
        this.springReflect = springReflect;
        list = springReflect.getSpringReflectContent().classWithSuper(SocketServerEvent.class).toList();
    }

    @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.debug(
                "channel open {}",
                ChannelUtil.getLocalAddress(ctx.channel()) + " : " + ChannelUtil.getRemoteAddress(ctx.channel())
        );
        /*TODO 构造函数自动注册*/
        SocketSession socketSession = new SocketSession(
                SocketSession.Type.server,
                ctx.channel(),
                ChannelUtil.attr(ctx.channel(), ChannelUtil.WEB_SOCKET_SESSION_KEY),
                socketServerConfig.isEnabledScheduledFlush()
        );
        if (socketServerConfig.getMaxFrameBytes() >= 0) {
            socketSession.setMaxFrameBytes(BytesUnit.Kb.toBytes(socketServerConfig.getMaxFrameBytes()));
        }
        socketSession.setMaxFrameLength(socketServerConfig.getMaxFrameLength());
        list.forEach(socketServerEvent -> socketServerEvent.onOpen(socketServer, socketSession));
    }

    @Override public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        SocketSession socketSession = ChannelUtil.session(ctx.channel());
        if (socketSession != null) {
            list.forEach(socketServerEvent -> socketServerEvent.onClose(socketServer, socketSession));
        }
    }

    @Override public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        SocketSession socketSession = ChannelUtil.session(ctx.channel());
        if (socketSession != null) {
            list.forEach(socketServerEvent -> socketServerEvent.onException(socketSession, cause));
        }
    }


}
