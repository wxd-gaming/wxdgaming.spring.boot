package wxdgaming.spring.boot.net;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.net.InetSocketAddress;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2021-01-11 14:27
 **/
@Slf4j
public class NioFactory implements Serializable {

    public static final byte[] EmptyBytes = new byte[0];

    /** __Session__ */
    static public final String Session = "__Session__";
    /** __AuthUser__ */
    static public final String AuthUser = "__AuthUser__";

    static public String ctxTostring(ChannelHandlerContext ctx) {
        String str;
        if (ctx == null) {
            str = "null";
        } else {
            Channel channel = ctx.channel();
            if (channel != null && channel.id() != null) {
                str = channel.id().asShortText();
            } else {
                str = "null";
            }
        }
        str += " - " + getRemoteAddress(ctx);
        return str;
    }

    static public String getCtxName(ChannelHandlerContext ctx) {
        String str;
        if (ctx == null) {
            str = "null";
        } else {
            Channel channel = ctx.channel();
            if (channel != null && channel.id() != null) {
                str = channel.id().asShortText();
            } else {
                str = "null";
            }
        }
        return str;
    }

    /**
     * 获取链接的ip地址
     *
     * @param session session
     * @return 地址
     */
    static public String getIP(ChannelHandlerContext session) {
        try {
            InetSocketAddress insocket = (InetSocketAddress) session.channel().remoteAddress();
            return insocket.getAddress().getHostAddress();
        } catch (Throwable ignore) {}
        return null;
    }

    static public String getRemoteAddress(ChannelHandlerContext session) {
        try {
            InetSocketAddress insocket = (InetSocketAddress) session.channel().remoteAddress();
            return insocket.getAddress().getHostAddress() + ":" + insocket.getPort();
        } catch (Throwable ignore) {}
        return null;
    }

    static public String getLocalAddress(ChannelHandlerContext session) {
        try {
            InetSocketAddress insocket = (InetSocketAddress) session.channel().localAddress();
            return insocket.getAddress().getHostAddress() + ":" + insocket.getPort();
        } catch (Throwable ignore) {}
        return null;
    }

    /**
     * 获取链接信息设置的参数
     *
     * @param <R>
     * @param ctx
     * @param key
     * @return
     */
    static public <R> R attr(ChannelHandlerContext ctx, String key) {
        if (ctx != null) {
            AttributeKey<Object> valueOf = AttributeKey.valueOf(key);
            if (ctx.channel().hasAttr(valueOf)) {
                Object object = ctx.channel().attr(valueOf).get();
                return (R) object;
            }
        }
        return null;
    }

    /**
     * 设置链接参数信息
     *
     * @param ctx
     * @param key
     * @param value
     */
    static public void attr(ChannelHandlerContext ctx, String key, Object value) {
        if (ctx != null) {
            ctx.channel().attr(AttributeKey.valueOf(key)).set(value);
        }
    }

    /**
     * 移除
     *
     * @param ctx
     * @param key
     */
    static public <R> R attrDel(ChannelHandlerContext ctx, String key) {
        if (ctx != null) {
            return (R) ctx.channel().attr(AttributeKey.valueOf(key)).getAndSet(null);
        }
        return null;
    }

}
