package wxdgaming.spring.boot.net.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.collection.concurrent.ConcurrentLoopList;
import wxdgaming.spring.boot.core.threading.DefaultExecutor;
import wxdgaming.spring.boot.core.threading.Event;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.boot.message.pojo.inner.InnerMessage;
import wxdgaming.spring.boot.net.BootstrapBuilder;
import wxdgaming.spring.boot.net.ISession;
import wxdgaming.spring.boot.net.SessionHandler;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ssl.WxdSslHandler;

import javax.net.ssl.SSLEngine;
import java.io.Closeable;
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
public abstract class SocketClient implements InitPrint, Closeable, ISession {

    protected Bootstrap bootstrap;

    protected final DefaultExecutor defaultExecutor;
    protected final BootstrapBuilder bootstrapBuilder;
    protected final SocketClientDeviceHandler socketClientDeviceHandler;
    protected final ClientMessageDecode clientMessageDecode;
    protected final ClientMessageEncode clientMessageEncode;

    protected final SocketClientBuilder socketClientBuilder;
    protected final SocketClientBuilder.Config config;
    /** 所有的连接 */
    protected final ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    /** 所有的连接 */
    protected final ConcurrentLoopList<SocketSession> sessions = new ConcurrentLoopList<>();

    public SocketClient(DefaultExecutor defaultExecutor,
                        BootstrapBuilder bootstrapBuilder,
                        SocketClientBuilder socketClientBuilder,
                        SocketClientBuilder.Config config,
                        SessionHandler sessionHandler,
                        ClientMessageDecode clientMessageDecode,
                        ClientMessageEncode clientMessageEncode) {
        this.defaultExecutor = defaultExecutor;
        this.bootstrapBuilder = bootstrapBuilder;
        this.socketClientBuilder = socketClientBuilder;
        this.config = config;
        this.socketClientDeviceHandler = new SocketClientDeviceHandler(bootstrapBuilder, sessionHandler);
        this.clientMessageDecode = clientMessageDecode;
        this.clientMessageEncode = clientMessageEncode;
        defaultExecutor.scheduleAtFixedRate(new Event() {
            @Override public void onEvent() throws Throwable {
                InnerMessage.ReqHeart reqHeart = new InnerMessage.ReqHeart().setMilli(MyClock.millis());
                writeAndFlush(reqHeart);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    public void init() {
        bootstrap = new Bootstrap();
        bootstrap.group(socketClientBuilder.getClientLoop())
                .channel(socketClientBuilder.getClient_Socket_Channel_Class())
                /*链接超时设置*/
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, this.config.getConnectTimeout())
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
                        if (bootstrapBuilder.isDebugLogger()) {
                            pipeline.addLast(new LoggingHandler("DEBUG"));/*设置log监听器，并且日志级别为debug，方便观察运行流程*/
                        }
                        if (config.isEnableSsl()) {
                            SSLEngine sslEngine = config.getSslContext().createSSLEngine();
                            sslEngine.setUseClientMode(true);
                            sslEngine.setNeedClientAuth(false);
                            pipeline.addFirst("sslhandler", new WxdSslHandler(sslEngine));
                        }
                        /*空闲链接检查*/
                        int idleTime = config.getIdleTimeout();
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

    @Start
    @Order(2000)
    public void start() {
        connect();
    }

    @Override public void close() {
        sessions.duplicate().forEach(session -> session.close("shutdown"));
    }

    public final SocketSession connect() {
        SocketSession socketSession = connect(null);
        sessions.add(socketSession);
        /*添加事件，如果链接关闭了触发*/
        socketSession.getChannel().closeFuture().addListener(future -> sessions.remove(socketSession));
        socketClientDeviceHandler.getSessionHandler().openSession(socketSession);
        return socketSession;
    }

    public SocketSession connect(Consumer<Channel> consumer) {
        CompletableFuture<SocketSession> completableFuture = new CompletableFuture<>();
        bootstrap.connect(config.getHost(), config.getPort())
                .addListener((ChannelFutureListener) future -> {
                    Throwable cause = future.cause();
                    if (cause != null) {
                        completableFuture.completeExceptionally(cause);
                        return;
                    }
                    Channel channel = future.channel();
                    SocketSession socketSession = new SocketSession(SocketSession.Type.client, channel, false);
                    socketSession.setSsl(config.isEnableSsl());
                    completableFuture.complete(socketSession);
                    log.debug("{} connect success {}", SocketClient.this.getClass().getSimpleName(), channel);
                    if (consumer != null) {
                        consumer.accept(channel);
                    }
                });

        try {
            return completableFuture.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
