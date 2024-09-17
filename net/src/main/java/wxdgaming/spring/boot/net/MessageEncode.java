package wxdgaming.spring.boot.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.net.message.PojoBase;
import wxdgaming.spring.boot.net.message.SerializerUtil;

/**
 * 消息编码
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-20 09:09
 **/
@Slf4j
public abstract class MessageEncode extends ChannelOutboundHandlerAdapter {

    private final MessageDispatcher messageDispatcher;

    public MessageEncode(MessageDispatcher messageDispatcher) {
        this.messageDispatcher = messageDispatcher;
    }

    @Override public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        SocketSession session = ChannelUtil.session(ctx.channel());
        switch (msg) {
            case String str -> {
                if (!session.isWebSocket()) {
                    log.info("{} 不是 websocket 不允许发送 string 类型 {}", ChannelUtil.ctxTostring(ctx), msg);
                    return;
                }
                TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(str);
                super.write(ctx, textWebSocketFrame, promise);
            }
            case PojoBase pojoBase -> {
                Integer msgId = messageDispatcher.getMessageName2Id().get(pojoBase.getClass().getName());
                if (msgId == null) {
                    log.error("{} 消息处理异常：{}", ChannelUtil.ctxTostring(ctx), pojoBase.getClass().getName());
                    return;
                }
                byte[] bytes = SerializerUtil.encode(pojoBase);
                ByteBuf byteBuf = build(msgId, bytes);
                if (session.isWebSocket()) {
                    super.write(ctx, new BinaryWebSocketFrame(byteBuf), promise);
                } else {
                    super.write(ctx, byteBuf, promise);
                }
            }
            case byte[] bytes -> {
                ByteBuf byteBuf = ByteBufUtil.pooledByteBuf(bytes.length);
                byteBuf.writeBytes(bytes);
                super.write(ctx, byteBuf, promise);
            }
            case null, default -> super.write(ctx, msg, promise);
        }
    }

    public static ByteBuf build(int messageId, byte[] bytes) {
        ByteBuf byteBuf = ByteBufUtil.pooledByteBuf(bytes.length + 10);
        byteBuf.writeInt(bytes.length + 4);
        byteBuf.writeInt(messageId);
        byteBuf.writeBytes(bytes);
        return byteBuf;
    }

}
