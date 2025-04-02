package wxdgaming.spring.boot.starter.net.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.starter.net.ChannelUtil;
import wxdgaming.spring.boot.starter.net.MessageDecode;
import wxdgaming.spring.boot.starter.net.pojo.ProtoListenerFactory;

/**
 * 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-17 15:07
 **/
@Slf4j
@ChannelHandler.Sharable
public class SocketServerMessageDecode extends MessageDecode {

    final SocketServerConfig config;

    public SocketServerMessageDecode(SocketServerConfig config, ProtoListenerFactory protoListenerFactory) {
        super(protoListenerFactory);
        this.config = config;
    }

    @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override protected void actionWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (!config.isEnabledWebSocket()) {
            ChannelUtil.closeSession(ctx.channel(), "不支持 WebSocket 服务");
            return;
        }
        super.actionWebSocketFrame(ctx, frame);
    }

    @Override protected void actionBytes(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        if (!config.isEnabledTcp()) {
            ChannelUtil.closeSession(ctx.channel(), "不支持 tcp socket 服务");
            return;
        }
        super.actionBytes(ctx, byteBuf);
    }

}
