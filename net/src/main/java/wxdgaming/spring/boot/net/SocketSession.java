package wxdgaming.spring.boot.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.message.PojoBase;

/**
 * socket session
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-16 08:58
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
public class SocketSession {

    private final Channel channel;
    private boolean webSocket;
    private boolean ssl;

    public SocketSession(Channel channel, Boolean webSocket) {
        this.channel = channel;
        this.webSocket = Boolean.TRUE.equals(webSocket);
        ChannelUtil.attr(this.channel, ChannelUtil.SOCKET_SESSION_KEY, this);
    }

    public void writeAndFlush(Object message) {
        channel.writeAndFlush(message);
    }

    public ChannelFuture writeAndFlush(PojoBase pojoBase) {
        return channel.writeAndFlush(pojoBase);
    }

    public ChannelFuture writeAndFlush(ByteBuf byteBuf) {
        if (webSocket) {
            BinaryWebSocketFrame webSocketFrame = new BinaryWebSocketFrame(byteBuf);
            return channel.writeAndFlush(webSocketFrame);
        } else {
            return channel.writeAndFlush(byteBuf);
        }
    }

    @Override public String toString() {
        return "【" + ChannelUtil.ctxTostring(channel) + ", webSocket=" + webSocket + ", ssl=" + ssl + "】";
    }
}
