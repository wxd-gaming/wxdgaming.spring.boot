package wxdgaming.spring.boot.starter.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.internal.OutOfDirectMemoryError;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.starter.core.GlobalUtil;

import java.util.Optional;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2020-12-26 15:03
 **/
@Slf4j
@Getter
public abstract class SocketDeviceHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);

    }

    @Override public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        log.debug(
                "channel close {}",
                ChannelUtil.getLocalAddress(ctx.channel()) + " : " + ChannelUtil.getRemoteAddress(ctx.channel())
        );
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent event) {
            switch (event.state()) {
                case IdleState.READER_IDLE: {
                    ChannelUtil.closeSession(ctx.channel(), "读空闲");
                }
                break;
                case IdleState.WRITER_IDLE: {
                    ChannelUtil.closeSession(ctx.channel(), "写空闲");
                }
                break;
                case IdleState.ALL_IDLE: {
                    ChannelUtil.closeSession(ctx.channel(), "读写空闲");
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
