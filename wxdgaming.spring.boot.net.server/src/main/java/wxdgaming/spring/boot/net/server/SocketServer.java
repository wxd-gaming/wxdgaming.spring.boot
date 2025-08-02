package wxdgaming.spring.boot.net.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.Throw;
import wxdgaming.spring.boot.core.ann.Shutdown;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.util.BytesUnit;
import wxdgaming.spring.boot.net.NioFactory;
import wxdgaming.spring.boot.net.SessionGroup;
import wxdgaming.spring.boot.net.pojo.ProtoListenerFactory;
import wxdgaming.spring.boot.net.ssl.WxdOptionalSslHandler;

import javax.net.ssl.SSLContext;


/**
 * socket 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-12 20:16
 **/
@Slf4j
@Getter
public class SocketServer implements InitPrint {

    protected SocketServerConfig config;
    protected final SessionGroup sessionGroup = new SessionGroup();
    protected ServerBootstrap bootstrap;
    protected ChannelFuture future;
    protected Channel serverChannel;
    protected ScheduledFuture<?> scheduledFuture;


    public SocketServer(SocketServerConfig config) {
        if (log.isDebugEnabled()) {
            log.debug("socket server config: {}", config.toJSONString());
        }
        this.config = config;
    }

    @Start
    @Order(1000)
    public void start(@Qualifier ProtoListenerFactory protoListenerFactory) {

        SocketServerDeviceHandler socketServerDeviceHandler = new SocketServerDeviceHandler(config, sessionGroup);
        SocketServerMessageDecode socketServerMessageDecode = new SocketServerMessageDecode(config, protoListenerFactory);
        SSLContext sslContext = config.sslContext();

        int writeBytes = (int) BytesUnit.Mb.toBytes(config.getWriteByteBufM());
        int recvBytes = (int) BytesUnit.Mb.toBytes(config.getRecvByteBufM());
        bootstrap = new ServerBootstrap().group(NioFactory.bossThreadGroup(), NioFactory.workThreadGroup())
                /*channel方法用来创建通道实例(NioServerSocketChannel类来实例化一个进来的链接)*/
                .channel(NioFactory.serverSocketChannelClass())
                /*方法用于设置监听套接字*/
                .option(ChannelOption.SO_BACKLOG, 0)
                /*地址重用，socket链接断开后，立即可以被其他请求使用*/
                .option(ChannelOption.SO_REUSEADDR, true)
                /*是否启用心跳保活机机制*/
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                /*方法用于设置和客户端链接的套接字*/
                .childOption(ChannelOption.TCP_NODELAY, true)
                /*发送缓冲区 影响 channel.isWritable()*/
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, new WriteBufferWaterMark((int) BytesUnit.Kb.toBytes(8), writeBytes))
                /*接收缓冲区，使用内存池*/
                .childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator((int) BytesUnit.Kb.toBytes(1), (int) BytesUnit.Kb.toBytes(8), recvBytes))
                /*为新链接到服务器的handler分配一个新的channel。ChannelInitializer用来配置新生成的channel。(如需其他的处理，继续ch.pipeline().addLast(新匿名handler对象)即可)*/
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    public void initChannel(SocketChannel socketChannel) throws Exception {
                        try {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            if (config.isDebug()) {
                                pipeline.addLast("logging", new LoggingHandler("DEBUG"));// 设置log监听器，并且日志级别为debug，方便观察运行流程
                            }

                            pipeline.addFirst(new WxdOptionalSslHandler(sslContext));

                            /*设置读取空闲*/
                            pipeline.addLast("idleHandler", config.idleStateHandler());
                            /* socket 选择器 区分是tcp websocket http*/
                            pipeline.addLast("socket-choose-handler", new SocketServerChooseHandler(config));
                            /*处理链接*/
                            pipeline.addLast("device-handler", socketServerDeviceHandler);
                            /*解码消息*/
                            pipeline.addLast("decode", socketServerMessageDecode);
                            /*解码消息*/
                            pipeline.addLast(
                                    "encode",
                                    new SocketServerMessageEncode(
                                            config.isEnabledScheduledFlush(),
                                            config.getScheduledDelayMs(),
                                            socketChannel,
                                            protoListenerFactory
                                    )
                            );
                            addChanelHandler(socketChannel, pipeline);
                        } catch (Exception e) {
                            log.error("SocketServer init fail port: {}", config.getPort(), e);
                            throw e;
                        }
                    }

                });

        try {
            future = bootstrap.bind(config.getPort());
            future.syncUninterruptibly();
            serverChannel = future.channel();
            log.info("SocketServer started at port {}", config.getPort());
        } catch (Exception e) {
            throw Throw.of("SocketServer start fail port: " + config.getPort(), e);
        }
    }

    @Shutdown
    @Order(100)
    public void shutdown() {
        if (scheduledFuture != null) {
            scheduledFuture.cancel(false);
        }
        if (future != null) {
            try {
                serverChannel.close();
                log.info("socket server: {}, port: {} close ", this.getClass().getSimpleName(), config.getPort());
            } catch (Exception ignored) {}
        }
    }

    protected void addChanelHandler(SocketChannel socketChannel, ChannelPipeline pipeline) {}

}
