package wxdgaming.spring.boot.net.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketFrameAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.system.BytesUnit;
import wxdgaming.spring.boot.net.ChannelUtil;

import java.util.List;

/**
 * 判定是 socket 还是 web socket
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-08-25 09:47
 **/
@Slf4j
public class SocketServerChooseHandler extends ByteToMessageDecoder {

    /** 默认暗号长度为23 */
    private static final int MAX_LENGTH = 23;
    /** WebSocket握手的协议前缀 */
    private static final String WEBSOCKET_PREFIX = "GET /";

    final SocketServerBuilder.Config config;

    public SocketServerChooseHandler(SocketServerBuilder.Config config) {
        this.config = config;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String protocol = getBufStart(in);
        if (protocol.startsWith(WEBSOCKET_PREFIX)) {
            if (!config.isEnableWebSocket()) {
                ctx.disconnect();
                ctx.close();
                throw new RuntimeException(ChannelUtil.ctxTostring(ctx) + " - 不支持 web socket or http ");
            }
            websocketAdd(ctx);
            // 对于 webSocket ，不设置超时断开
            // ctx.pipeline().remove(IdleStateHandler.class);
            // ctx.pipeline().remove(LengthFieldBasedFrameDecoder.class);
        }
        in.resetReaderIndex();
        ctx.pipeline().remove(this.getClass());
    }

    private String getBufStart(ByteBuf in) {
        int length = in.readableBytes();
        if (length > MAX_LENGTH) {
            length = MAX_LENGTH;
        }
        // 标记读位置
        in.markReaderIndex();
        byte[] content = new byte[length];
        in.readBytes(content);
        return new String(content);
    }

    public void websocketAdd(ChannelHandlerContext ctx) {
        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        ctx.pipeline().addBefore("device-handler", "http-codec", new HttpServerCodec());
        ctx.pipeline().addBefore("device-handler", "http-object-aggregator", new HttpObjectAggregator((int) BytesUnit.Mb.toBytes(64)));/*接受完整的http消息 64mb*/
        // ChunkedWriteHandler：向客户端发送HTML5文件,文件过大会将内存撑爆
        ctx.pipeline().addBefore("device-handler", "http-chunked", new ChunkedWriteHandler());
        ctx.pipeline().addBefore("device-handler", "WebSocketAggregator", new WebSocketFrameAggregator((int) BytesUnit.Mb.toBytes(64)));
        // 用于处理websocket, /ws为访问websocket时的uri
        ctx.pipeline().addBefore("device-handler", "ProtocolHandler", new WebSocketServerProtocolHandler(config.getWebSocketPrefix(), null, false, 64 * 1024 * 1024));
        ChannelUtil.session(ctx.channel()).setWebSocket(true);
    }

}
