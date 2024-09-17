package wxdgaming.spring.boot.broker;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.net.BootstrapBuilder;
import wxdgaming.spring.boot.net.MessageDispatcher;
import wxdgaming.spring.boot.net.SessionGroup;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.server.ServerMessageDecode;

/**
 * 代理解码
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-17 16:36
 **/
@ChannelHandler.Sharable
public class BrokerMessageDecode extends ServerMessageDecode implements InitPrint {

    final SessionGroup sessionGroup;

    public BrokerMessageDecode(BootstrapBuilder bootstrapBuilder, MessageDispatcher dispatcher, SessionGroup sessionGroup) {
        super(bootstrapBuilder, dispatcher);
        this.sessionGroup = sessionGroup;
    }

    @Override protected void readBytes0(ChannelHandlerContext ctx, ByteBuf byteBuf) throws Exception {
        // 读取 消息长度（int）和消息ID（int） 需要 8 个字节
        while (byteBuf.readableBytes() >= 8) {
            // 读取消息长度
            byteBuf.markReaderIndex();
            int len = byteBuf.getInt(0);
            if (len > 0 && byteBuf.readableBytes() >= len) {
                /*TODO 选择压缩*/
                // byte isZip = tmpByteBuf.readByte();
                byte[] messageBytes = new byte[len + 4];
                /*读取报文类容*/
                byteBuf.readBytes(messageBytes);
                sessionGroup.writeAndFlush(messageBytes);
            } else {
                /*重新设置读取进度*/
                byteBuf.resetReaderIndex();
                break;
            }
        }
    }

    @Override protected void action(SocketSession socketSession, int messageId, byte[] messageBytes) throws Exception {
    }

}
