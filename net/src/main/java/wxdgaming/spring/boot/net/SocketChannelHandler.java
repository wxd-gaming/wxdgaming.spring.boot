package wxdgaming.spring.boot.net;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.OutOfDirectMemoryError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wxdgaming.spring.boot.core.GlobalUtil;
import wxdgaming.spring.boot.net.util.ByteBufUtil;

import java.net.URI;
import java.util.Optional;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2020-12-26 15:03
 **/
public class SocketChannelHandler extends ChannelInboundHandlerAdapter {

    private static final Logger log = LoggerFactory.getLogger(SocketChannelHandler.class);

    private final boolean autoRelease;

    /**
     * @param autoRelease 是否自动调用{@link ByteBuf#release()}
     */
    public SocketChannelHandler(boolean autoRelease) {
        this.autoRelease = autoRelease;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        if (log.isDebugEnabled())
            log.debug("channel 接入 {} {}", NioFactory.getCtxName(ctx), ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        if (log.isDebugEnabled())
            log.debug("channel 激活 {} {}", NioFactory.getCtxName(ctx), ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        if (log.isDebugEnabled())
            log.debug("channel 空闲 {} {}", NioFactory.getCtxName(ctx), ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        if (log.isDebugEnabled())
            log.debug("channel 关闭 {} {}", NioFactory.getCtxName(ctx), ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt);
        if (evt instanceof IdleStateEvent event) {
            String ctxName = NioFactory.getCtxName(ctx) + " - " + NioFactory.getIP(ctx);
            switch (event.state()) {
                case READER_IDLE: {
                    ctx.disconnect();
                    ctx.close();
                    log.info("读空闲 {}", ctxName);
                }
                break;
                case WRITER_IDLE: {
                    ctx.disconnect();
                    ctx.close();
                    log.info("写空闲 {}", ctxName);
                }
                break;
                case ALL_IDLE: {
                    /*写空闲的计数加1*/
                    ctx.disconnect();
                    ctx.close();
                    log.info("读写空闲 {}", ctxName);
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
        String ctxName = NioFactory.getCtxName(ctx) + " - " + NioFactory.getIP(ctx);
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

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        try {
            ctx.flush();
        } finally {
            super.channelReadComplete(ctx);
        }
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
            case FullHttpRequest fullHttpRequest -> {
                // 以http请求形式接入，但是走的是websocket
                handleHttpRequest(ctx, fullHttpRequest);
                break;
            }
            case WebSocketFrame webSocketFrame -> {
                // 处理websocket客户端的消息
                handlerWebSocketFrame(ctx, webSocketFrame);
                break;
            }
            case ByteBuf byteBuf -> {
                read(ctx, byteBuf);
                break;
            }
            default -> {
                log.error("未知处理类型：{}", object.getClass().getName());
            }
        }
    }

    WebSocketServerHandshakerFactory wsFactory;

    WebSocketServerHandshaker handshaker = null;

    protected void handlerWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        try {
            if (frame instanceof CloseWebSocketFrame closeWebSocketFrame/*判断是否关闭链路的指令*/) {
                handshaker.close(ctx.channel(), closeWebSocketFrame.retain());
            } else if (frame instanceof PingWebSocketFrame /*判断是否ping消息*/) {
                ctx.writeAndFlush(new PongWebSocketFrame(frame.content().retain()));
            } else if (frame instanceof BinaryWebSocketFrame binaryWebSocketFrame/*二进制数据*/) {
                ByteBuf byteBuf = Unpooled.wrappedBuffer(binaryWebSocketFrame.content());
                read(ctx, byteBuf);
            } else if (frame instanceof TextWebSocketFrame textWebSocketFrame/*文本数据*/) {
                String request = textWebSocketFrame.text();
                log.debug("当前不接受文本消息：{}, {}", NioFactory.ctxTostring(ctx), request);
            }
        } catch (Throwable e) {
            log.warn("处理消息异常", e);
        }
    }

    protected void read(ChannelHandlerContext ctx, ByteBuf byteBuf) {

    }

    /**
     * 唯一的一次http请求，用于创建websocket
     */
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest httpRequest) {
        try {
            URI uri = new URI(httpRequest.uri());
            String path = uri.getPath();

            String cookieString = httpRequest.headers().get(HttpHeaderNames.COOKIE);

            if (log.isInfoEnabled()) {
                StringBuilder stringBuilder = new StringBuilder();

                log.info(
                        stringBuilder
                                .append("\n")
                                .append("=============================================================================================").append("\n")
                                .append("Host：Web Socket ").append(NioFactory.ctxTostring(ctx)).append(uri).append(";\n")
                                .append("User-Agent：").append(httpRequest.headers().get(HttpHeaderNames.USER_AGENT)).append(";\n")
                                .append("Accept-Encoding：").append(httpRequest.headers().get(HttpHeaderNames.ACCEPT_ENCODING)).append(";\n")
                                .append(HttpHeaderNames.COOKIE).append("：").append(cookieString).append(";\n")
                                .append("=============================================================================================")
                                .toString()
                );
            }

            if (!httpRequest.decoderResult().isSuccess()
                    || (!"websocket".equalsIgnoreCase(httpRequest.headers().get("Upgrade")))/*判别必须websocket不能是get或者post*/) {
                // 若不是websocket方式，则创建BAD_REQUEST的req，返回给客户端
                log.warn("收到的监听不正确，拒绝 请求 -> {}", path);
            } else {
                String accept_Encoding = httpRequest.headers().get(HttpHeaderNames.ACCEPT_ENCODING);
                if (accept_Encoding != null && accept_Encoding.contains("gzip")) {
                }
                String content_Encoding = httpRequest.headers().get(HttpHeaderNames.CONTENT_ENCODING);
                if (content_Encoding != null && content_Encoding.contains("gzip")) {
                }
                handshaker = wsFactory.newHandshaker(httpRequest);
                final Channel channel = ctx.channel();
                if (handshaker != null) {
                    /*todo 可以在这里设置回复客户端的 header参数 */
                    handshaker.handshake(channel, httpRequest, (HttpHeaders) null, channel.newPromise());
                    log.info("WebSocket Server 握手成功 {}", NioFactory.ctxTostring(ctx));
                    return;
                }
            }
        } catch (Exception e) {
            log.warn("解析异常", e);
        }
        sendHttpResponse(ctx, httpRequest, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST));
    }

    /**
     * 拒绝不合法的请求，并返回错误信息
     */
    private void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, DefaultFullHttpResponse response) {

        // 返回应答给客户端
        if (response.status().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(response.status().toString(), CharsetUtil.UTF_8);
            response.content().writeBytes(buf);
            ByteBufUtil.release(buf);
        }
        ChannelFuture channelFuture = ctx.writeAndFlush(response);
        // 如果是非Keep-Alive，关闭链接
        if (response.status().code() != 200 || req == null || !HttpUtil.isKeepAlive(req)) {
            channelFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

}
