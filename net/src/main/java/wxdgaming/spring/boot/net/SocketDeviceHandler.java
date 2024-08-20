package wxdgaming.spring.boot.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.internal.OutOfDirectMemoryError;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wxdgaming.spring.boot.core.GlobalUtil;

import java.util.Optional;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2020-12-26 15:03
 **/
@Getter
public abstract class SocketDeviceHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SocketDeviceHandler.class);


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        if (log.isDebugEnabled())
            log.debug("channel 接入 {} {}", ChannelUtil.ctxTostring(ctx), ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        if (log.isDebugEnabled())
            log.debug("channel 关闭 {} {}", ChannelUtil.ctxTostring(ctx), ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent event) {
            String ctxName = ChannelUtil.session(ctx.channel()).toString();
            switch (event.state()) {
                case READER_IDLE: {
                    log.info("读空闲 {}", ctxName);
                    ctx.disconnect();
                    ctx.close();
                }
                break;
                case WRITER_IDLE: {
                    log.info("写空闲 {}", ctxName);
                    ctx.disconnect();
                    ctx.close();
                }
                break;
                case ALL_IDLE: {
                    log.info("读写空闲 {}", ctxName);
                    /*写空闲的计数加1*/
                    ctx.disconnect();
                    ctx.close();
                }
                break;
            }
        }
    }

    /**
     * 发现异常
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String message = Optional.ofNullable(cause.getMessage())
                .map(String::toLowerCase).orElse("");
        String ctxName = ChannelUtil.ctxTostring(ctx);
        if (message.contains("sslhandshak")
                || message.contains("sslexception")
                || message.contains("certificate_unknown")
                || message.contains("connection reset")
                || message.contains("你的主机中的软件中止了一个已建立的连接")
                || message.contains("远程主机强迫关闭了一个现有的连接")) {
            if (log.isDebugEnabled()) {
                log.debug("内部处理异常：{}, {}", message, ctxName);
            }
        } else {
            log.warn("内部异常：{}", ctxName, cause);
            if (cause instanceof OutOfDirectMemoryError) {
                GlobalUtil.exception(ctxName, cause);
            }
        }
    }

}
