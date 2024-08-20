package wxdgaming.spring.boot.net.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.net.BootstrapConfig;
import wxdgaming.spring.boot.net.SocketSession;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
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
public class SocketClient {

    protected final BootstrapConfig bootstrapConfig;
    protected final SocketClientDeviceHandler socketClientDeviceHandler;
    protected final ClientMessageDecode clientMessageDecode;
    protected final ClientMessageEncode clientMessageEncode;
    @Setter protected String host;
    @Setter protected int port;
    protected Bootstrap bootstrap;

    public SocketClient(BootstrapConfig bootstrapConfig,
                        SocketClientDeviceHandler socketClientDeviceHandler,
                        ClientMessageDecode clientMessageDecode,
                        ClientMessageEncode clientMessageEncode) {
        this.bootstrapConfig = bootstrapConfig;
        this.socketClientDeviceHandler = socketClientDeviceHandler;
        this.clientMessageDecode = clientMessageDecode;
        this.clientMessageEncode = clientMessageEncode;
    }

    public void init() {
        bootstrap = new Bootstrap();
        bootstrap.group(bootstrapConfig.getClientLoop())
                .channel(bootstrapConfig.getClient_Socket_Channel_Class())
                /*链接超时设置*/
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, bootstrapConfig.getClientConnectTimeOut())
                /*是否启用心跳保活机机制*/
                .option(ChannelOption.SO_KEEPALIVE, true)
                /*发送缓冲区 影响 channel.isWritable()*/
                .option(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(1, 64 * 1024 * 1024))
                /*使用内存池*/
                .option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 64 * 1024 * 1024))
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        if (bootstrapConfig.isDebugLogger()) {
                            pipeline.addLast(new LoggingHandler("DEBUG"));/*设置log监听器，并且日志级别为debug，方便观察运行流程*/
                        }
                        /*空闲链接检查*/
                        int idleTime = bootstrapConfig.getServerSessionIdleTime();
                        if (idleTime > 0) {
                            pipeline.addLast(new IdleStateHandler(0, 0, idleTime, TimeUnit.SECONDS));
                        }
                        /*处理链接*/
                        pipeline.addLast("device-handler", socketClientDeviceHandler);
                        /*解码消息*/
                        pipeline.addLast("decode", clientMessageDecode);
                        /*解码消息*/
                        pipeline.addLast("encode", clientMessageEncode);
                        addChanelHandler(socketChannel, pipeline);
                    }
                });
    }

    protected void addChanelHandler(SocketChannel socketChannel, ChannelPipeline pipeline) {}

    public SocketSession connect() {
        return connect(null);
    }

    public SocketSession connect(Consumer<Channel> consumer) {
        CompletableFuture<SocketSession> completableFuture = new CompletableFuture<>();
        bootstrap.connect(host, port)
                .addListener(new ChannelFutureListener() {

                    @Override public void operationComplete(ChannelFuture future) throws Exception {
                        Throwable cause = future.cause();
                        if (cause != null) {
                            completableFuture.completeExceptionally(cause);
                            return;
                        }
                        Channel channel = future.channel();
                        SocketSession socketSession = new SocketSession(channel, false);
                        completableFuture.complete(socketSession);
                        log.info("connect success {}", channel);
                        if (consumer != null) {
                            consumer.accept(channel);
                        }
                    }

                });

        try {
            return completableFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
