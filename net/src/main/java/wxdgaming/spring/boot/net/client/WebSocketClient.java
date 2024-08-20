package wxdgaming.spring.boot.net.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.stream.ChunkedWriteHandler;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.system.BytesUnit;
import wxdgaming.spring.boot.net.BootstrapConfig;
import wxdgaming.spring.boot.net.SocketSession;

import java.net.URI;
import java.util.function.Consumer;

/**
 * tcp client
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-15 13:12
 **/
@Slf4j
@Getter
@Accessors(chain = true)
@Service
@ConfigurationProperties("client.web-socket")
@ConditionalOnProperty(prefix = "client.web-socket.config", name = "host")
public class WebSocketClient extends SocketClient {

    @Setter private String prefix = "/websocket";
    private WebSocketClientHandshaker handshaker;
    /** 包含的http head参数 */
    protected final HttpHeaders httpHeaders = new DefaultHttpHeaders();

    @Autowired
    public WebSocketClient(BootstrapConfig bootstrapConfig,
                           SocketClientDeviceHandler socketClientDeviceHandler,
                           ClientMessageDecode clientMessageDecode,
                           ClientMessageEncode clientMessageEncode) {
        super(bootstrapConfig, socketClientDeviceHandler, clientMessageDecode, clientMessageEncode);
    }

    @PostConstruct
    @Override public void init() {
        super.init();
        handshaker = WebSocketClientHandshakerFactory.newHandshaker(
                URI.create("ws://" + getConfig().getHost() + ":" + getConfig().getPort() + prefix),
                WebSocketVersion.V13,
                null,
                false,
                httpHeaders,
                (int) BytesUnit.Mb.toBytes(64)/*64 mb*/
        );
    }

    @Override protected void addChanelHandler(SocketChannel socketChannel, ChannelPipeline pipeline) {
        super.addChanelHandler(socketChannel, pipeline);
        // HttpServerCodec：将请求和应答消息解码为HTTP消息
        pipeline.addBefore("device-handler", "http-codec", new HttpClientCodec());
        pipeline.addBefore("device-handler", "http-object-aggregator", new HttpObjectAggregator((int) BytesUnit.Mb.toBytes(64)));/*接受完整的http消息 64mb*/
        // ChunkedWriteHandler：向客户端发送HTML5文件,文件过大会将内存撑爆
        pipeline.addBefore("device-handler", "http-chunked", new ChunkedWriteHandler());
        pipeline.addBefore("device-handler", "websocket-aggregator", new WebSocketFrameAggregator((int) BytesUnit.Mb.toBytes(64)));
        pipeline.addBefore("device-handler", "ProtocolHandler", new WebSocketClientProtocolHandler(handshaker));
        // handshaker.handshake(socketChannel);
    }

    @Override public SocketSession connect(Consumer<Channel> consumer) {
        SocketSession connect = super.connect(consumer);
        connect.setWebSocket(true);
        return connect;
    }

    SocketSession session;

    @Start
    @Order(2000)
    public void start() {
        session = connect();
    }
}
