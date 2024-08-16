package wxdgaming.spring.boot.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.util.AttributeKey;
import wxdgaming.spring.boot.core.LogbackUtil;

/**
 * 消息处理器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-15 11:03
 **/
public interface MessageAction {

    static final AttributeKey<ByteBuf> byteBufAttributeKey = AttributeKey.<ByteBuf>valueOf("__ctx_byteBuf__");

    default void readBytes(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
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

    default void readBytes0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        // 读取 消息长度（int）和消息ID（int） 需要 8 个字节
        while (byteBuf.readableBytes() >= 8) {
            // 读取消息长度
            byteBuf.markReaderIndex();
            int len = byteBuf.readInt();
            if (len > 0 && byteBuf.readableBytes() >= len) {
                /*读取消息ID*/
                int messageId = byteBuf.readInt();
                /*选择压缩*/
                // byte isZip = tmpByteBuf.readByte();
                byte[] messageBytes = new byte[len - 4];
                /*读取报文类容*/
                byteBuf.readBytes(messageBytes);
                SocketSession session = ChannelUtil.session(ctx.channel());
                action(session, messageId, messageBytes);
            } else {
                /*重新设置读取进度*/
                byteBuf.resetReaderIndex();
                break;
            }
        }
    }


    default void action(SocketSession socketSession, int messageId, byte[] messageBytes) throws Exception {
        LogbackUtil.logger().info("收到消息：ctx={}, id={}, bytes len={}", socketSession.toString(), messageId, messageBytes.length);
    }

    default void action(SocketSession socketSession, String message) throws Exception {
        LogbackUtil.logger().info("收到消息：ctx={}, message={}", socketSession.toString(), message);
    }

    default void httpRequest(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) throws Exception {

    }

}
