package wxdgaming.spring.boot.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.AttributeKey;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 消息解码，收到消息处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-20 09:09
 **/
@Slf4j
@Getter
public abstract class MessageDecode extends ChannelInboundHandlerAdapter {

    public static final AttributeKey<ByteBuf> byteBufAttributeKey = AttributeKey.<ByteBuf>valueOf("__ctx_byteBuf__");

    protected final boolean autoRelease;
    protected final MessageDispatcher dispatcher;

    public MessageDecode(boolean autoRelease, MessageDispatcher dispatcher) {
        this.autoRelease = autoRelease;
        this.dispatcher = dispatcher;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        boolean release = false;
        try {
            channelRead0(ctx, msg);
        } catch (Throwable throwable) {
            release = true;
            throw throwable;
        } finally {
            if (autoRelease || release) {
                ByteBufUtil.release(msg);
            }
        }
    }

    protected void channelRead0(ChannelHandlerContext ctx, Object object) throws Exception {
        switch (object) {
            case WebSocketFrame webSocketFrame -> {
                // 处理websocket客户端的消息
                handlerWebSocketFrame(ctx, webSocketFrame);
                break;
            }
            case ByteBuf byteBuf -> {
                readBytes(ctx, byteBuf);
                break;
            }
            default -> {
                if (log.isDebugEnabled()) {
                    log.debug("{} 未知处理类型：{}", ChannelUtil.ctxTostring(ctx), object.getClass().getName());
                }
                ctx.disconnect();
                ctx.close();
            }
        }
    }

    protected void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        try {
            SocketSession session = ChannelUtil.session(ctx.channel());
            switch (frame) {
                case PingWebSocketFrame pingWebSocketFrame ->
                    /*判断是否ping消息*/
                        ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
                case BinaryWebSocketFrame binaryWebSocketFrame -> {
                    /*二进制数据*/
                    ByteBuf byteBuf = Unpooled.wrappedBuffer(binaryWebSocketFrame.content());
                    readBytes(ctx, byteBuf);
                }
                case TextWebSocketFrame textWebSocketFrame -> {
                    /*文本数据*/
                    String request = textWebSocketFrame.text();
                    if (!session.checkReceiveMessage(request.length())) {
                        return;
                    }
                    dispatcher.getStringDispatcher().accept(session, request);
                }
                default -> log.warn("无法处理：{}", frame.getClass().getName());
            }
        } catch (Throwable e) {
            log.error("处理消息异常", e);
        }
    }

    protected void readBytes(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        /*netty底层每一次传递的bytebuf都是最新的所以必须缓存*/
        ByteBuf tmpByteBuf = ChannelUtil.attrDel(ctx.channel(), byteBufAttributeKey);
        if (tmpByteBuf == null) {
            tmpByteBuf = byteBuf;
        } else {
            tmpByteBuf = tmpByteBuf.writeBytes(byteBuf);
            ByteBufUtil.release(byteBuf);
        }

        readBytes0(ctx, tmpByteBuf);

        if (tmpByteBuf.readableBytes() > 0) {
            tmpByteBuf.discardReadBytes();
            ChannelUtil.attr(ctx.channel(), byteBufAttributeKey, tmpByteBuf);
        } else {
            ByteBufUtil.release(tmpByteBuf);
        }
    }

    protected void readBytes0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        // 读取 消息长度（int）和消息ID（int） 需要 8 个字节
        while (byteBuf.readableBytes() >= 8) {
            // 读取消息长度
            byteBuf.markReaderIndex();
            int len = byteBuf.readInt();
            if (len > 0 && byteBuf.readableBytes() >= len) {
                /*读取消息ID*/
                int messageId = byteBuf.readInt();
                /*TODO 选择压缩*/
                // byte isZip = tmpByteBuf.readByte();
                byte[] messageBytes = new byte[len - 4];
                /*读取报文类容*/
                byteBuf.readBytes(messageBytes);
                SocketSession session = ChannelUtil.session(ctx.channel());
                if (!session.checkReceiveMessage(messageBytes.length)) {
                    return;
                }
                dispatcher.dispatch(session, messageId, messageBytes);
            } else {
                /*重新设置读取进度*/
                byteBuf.resetReaderIndex();
                break;
            }
        }
    }


}
