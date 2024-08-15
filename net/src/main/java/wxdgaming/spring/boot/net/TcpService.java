package wxdgaming.spring.boot.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.system.BytesUnit;
import wxdgaming.spring.boot.core.system.JvmUtil;
import wxdgaming.spring.boot.net.ssl.WxOptionalSslHandler;

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
@Service
@ConditionalOnProperty("server.tcp.tcpPort")
public class TcpService implements Closeable, InitPrint {

    private final ServerBootstrapConfig serverBootstrapConfig;
    private ServerBootstrap bootstrap = null;
    private ChannelFuture future = null;

    public TcpService(ServerBootstrapConfig serverBootstrapConfig) {
        this.serverBootstrapConfig = serverBootstrapConfig;
    }

    public void init() {
        bootstrap = new ServerBootstrap();
        bootstrap.group(this.serverBootstrapConfig.getBossLoop(), this.serverBootstrapConfig.getWorkerLoop());
        /*channel方法用来创建通道实例( NioServerSocketChannel 类来实例化一个进来的链接)*/
        bootstrap.channel(this.serverBootstrapConfig.getServer_Socket_Channel_Class())
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
                        if (JvmUtil.getProperty(JvmUtil.Netty_Debug_Logger, false, Boolean::parseBoolean)) {
                            pipeline.addLast("logging", new LoggingHandler("DEBUG"));// 设置log监听器，并且日志级别为debug，方便观察运行流程
                        }

                        pipeline.addFirst(new WxOptionalSslHandler(serverBootstrapConfig.getSslContext()));

                        int idleTime = serverBootstrapConfig.getServerSessionIdleTime();
                        if (idleTime > 0) {
                            /*设置15秒的读取空闲*/
                            pipeline.addLast("idlehandler", new IdleStateHandler(idleTime, 0, 0, TimeUnit.SECONDS));
                        }

                    }

                });
    }

    @Start
    public void start() {

        log.info("开启 tcp 服务 {}", this.serverBootstrapConfig.getTcpPort());
    }

    /**
     * Closes this stream and releases any system resources associated
     * with it. If the stream is already closed then invoking this
     * method has no effect.
     * <p> As noted in {@link AutoCloseable#close()}, cases where the
     * close may fail require careful attention. It is strongly advised
     * to relinquish the underlying resources and to internally
     * <em>mark</em> the {@code Closeable} as closed, prior to throwing
     * the {@code IOException}.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override public void close() throws IOException {

    }
}
