package wxdgaming.spring.boot.net;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.net.message.PojoBase;

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

    public enum Type {
        server,
        client,
    }

    private final Type type;
    private final Channel channel;
    private boolean webSocket;
    private boolean ssl;

    public SocketSession(Type type, Channel channel, Boolean webSocket) {
        this.type = type;
        this.channel = channel;
        this.webSocket = Boolean.TRUE.equals(webSocket);
        ChannelUtil.attr(this.channel, ChannelUtil.SOCKET_SESSION_KEY, this);
    }

    public <R> R attribute(String key) {
        return ChannelUtil.attr(this.channel, key);
    }

    public void attribute(String key, Object value) {
        ChannelUtil.attr(this.channel, key, value);
    }

    public String getIP() {
        return ChannelUtil.getIP(this.channel);
    }

    public ChannelFuture writeAndFlush(Object message) {
        return channel.writeAndFlush(message);
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

    public ChannelFuture write(Object message) {
        return channel.write(message);
    }

    public ChannelFuture write(PojoBase pojoBase) {
        return channel.write(pojoBase);
    }

    public ChannelFuture write(ByteBuf byteBuf) {
        if (webSocket) {
            BinaryWebSocketFrame webSocketFrame = new BinaryWebSocketFrame(byteBuf);
            return channel.write(webSocketFrame);
        } else {
            return channel.write(byteBuf);
        }
    }

    public void flush() {
        channel.flush();
    }

    /** 是否可用 */
    public boolean isOpen() {
        return channel.isRegistered() && channel.isOpen();
    }

    public void close(String string) {
        try {channel.disconnect();} catch (Exception ignore) {}
        try {channel.close();} catch (Exception ignore) {}
        try {channel.deregister();} catch (Exception ignore) {}
        log.info("close {} {}", toString(), string);
    }

    @Override public String toString() {
        return """
                【%s - %s%s%s】""".formatted(
                type.name(),
                ChannelUtil.ctxTostring(channel),
                isWebSocket() ? ", websocket" : "",
                isSsl() ? ", ssl" : ""
        );
    }
}
