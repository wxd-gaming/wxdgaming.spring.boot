package wxdgaming.spring.boot.net.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.collection.concurrent.ConcurrentLoopList;
import wxdgaming.spring.boot.core.system.BytesUnit;
import wxdgaming.spring.boot.net.BootstrapBuilder;
import wxdgaming.spring.boot.net.ISession;
import wxdgaming.spring.boot.net.SessionHandler;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ssl.WxdOptionalSslHandler;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * netty tcp serve
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-14 20:30
 **/
@Slf4j
@Getter
public class SocketService implements InitPrint, Closeable, ISession {

    private final BootstrapBuilder bootstrapBuilder;
    private final SocketServerBuilder socketServerBuilder;
    private final SocketServerBuilder.Config config;
    private final SocketServerDeviceHandler socketServerDeviceHandler;
    private final ServerMessageDecode serverMessageDecode;
    private final ServerMessageEncode serverMessageEncode;
    private ServerBootstrap bootstrap = null;
    private ChannelFuture future = null;
    /** 所有的连接 */
    protected final ConcurrentLoopList<SocketSession> sessions = new ConcurrentLoopList<>();

    public SocketService(BootstrapBuilder bootstrapBuilder,
                         SocketServerBuilder socketServerBuilder,
                         SocketServerBuilder.Config config,
                         SessionHandler sessionHandler,
                         ServerMessageDecode serverMessageDecode,
                         ServerMessageEncode serverMessageEncode) {
        this.bootstrapBuilder = bootstrapBuilder;
        this.socketServerBuilder = socketServerBuilder;
        this.config = config;
        this.socketServerDeviceHandler = new SocketServerDeviceHandler(bootstrapBuilder, sessionHandler, this);
        this.serverMessageDecode = serverMessageDecode;
        this.serverMessageEncode = serverMessageEncode;
    }

    @PostConstruct
    public void init() {
        bootstrap = new ServerBootstrap();
        bootstrap.group(this.socketServerBuilder.getBossLoop(), this.socketServerBuilder.getWorkerLoop());
        /*channel方法用来创建通道实例( NioServerSocketChannel 类来实例化一个进来的链接)*/
        bootstrap.channel(this.socketServerBuilder.getServer_Socket_Channel_Class())
                /*方法用于设置监听套接字*/
                .option(ChannelOption.SO_BACKLOG, 0)
                /*地址重用，socket链接断开后，立即可以被其他请求使用*/
                .option(ChannelOption.SO_REUSEADDR, true)
                /*方法用于设置和客户端链接的套接字*/
                .childOption(ChannelOption.TCP_NODELAY, true)
                /*是否启用心跳保活机机制*/
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                /*地址重用，socket链接断开后，立即可以被其他请求使用*/
                .childOption(ChannelOption.SO_REUSEADDR, true)
                /*发送缓冲区 影响 channel.isWritable()*/
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark(1, (int) BytesUnit.Mb.toBytes(12)))
                /*接收缓冲区，使用内存池*/
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(512, 2048, (int) BytesUnit.Mb.toBytes(12)))
                /*为新链接到服务器的handler分配一个新的channel。ChannelInitializer用来配置新生成的channel。(如需其他的处理，继续ch.pipeline().addLast(新匿名handler对象)即可)*/
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        if (bootstrapBuilder.isDebugLogger()) {
                            pipeline.addLast(new LoggingHandler("DEBUG"));// 设置log监听器，并且日志级别为debug，方便观察运行流程
                        }

                        pipeline.addFirst(new WxdOptionalSslHandler(config.getSslContext()));

                        int idleTime = config.getIdleTimeout();
                        if (idleTime > 0) {
                            /*设置15秒的读取空闲*/
                            pipeline.addLast(new IdleStateHandler(idleTime, 0, 0, TimeUnit.SECONDS));
                        }
                        /* socket 选择器 区分是tcp websocket http*/
                        pipeline.addLast("socket-choose-handler", new SocketServerChooseHandler(config));
                        /*处理链接*/
                        pipeline.addLast("device-handler", socketServerDeviceHandler);
                        /*解码消息*/
                        pipeline.addLast("decode", serverMessageDecode);
                        /*解码消息*/
                        pipeline.addLast("encode", serverMessageEncode);
                    }

                });
    }

    @Start()
    @Order(1000)
    public void start() {
        this.future = bootstrap.bind(this.config.getPort());
        this.future.syncUninterruptibly();
        log.info("开启 socket 服务 {}", this.config.getPort());
    }

    /**
     * 关闭
     */
    @Override public void close() throws IOException {
        if (this.future != null) {
            this.future.channel().close();
        }
        log.info("关闭 socket 服务 {}", this.config.getPort());
    }
}
