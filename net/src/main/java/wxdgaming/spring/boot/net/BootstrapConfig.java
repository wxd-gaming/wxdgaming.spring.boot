package wxdgaming.spring.boot.net;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ssl.SslContextServer;
import wxdgaming.spring.boot.core.ssl.SslProtocolType;
import wxdgaming.spring.boot.core.threading.ThreadNameFactory;
import wxdgaming.spring.boot.net.client.ClientMessageDecode;
import wxdgaming.spring.boot.net.client.ClientMessageEncode;
import wxdgaming.spring.boot.net.client.SocketClientDeviceHandler;
import wxdgaming.spring.boot.net.server.ServerMessageDecode;
import wxdgaming.spring.boot.net.server.ServerMessageEncode;
import wxdgaming.spring.boot.net.server.SocketServerDeviceHandler;

import javax.net.ssl.SSLContext;

/**
 * 配置项
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-14 20:33
 **/
@Slf4j
@Getter
@Setter
@Configuration
// @ConditionalOnProperty("server.socket")
@ConfigurationProperties("server.socket")
public class BootstrapConfig implements InitPrint {

    private boolean debugLogger = false;

    private int tcpPort = 18001;
    private int bossThreadSize = 2;
    private int workerThreadSize = 10;
    private int serverSessionIdleTime = 20;
    private String webSocketPrefix = "/websocket";

    private int clientConnectTimeOut = 2000;
    private int clientThreadSize = 1;

    private SSLContext sslContext = null;

    private EventLoopGroup bossLoop;
    private EventLoopGroup workerLoop;
    private EventLoopGroup clientLoop;

    /** 服务监听的channel */
    private Class<? extends ServerChannel> Server_Socket_Channel_Class;
    private Class<? extends SocketChannel> Client_Socket_Channel_Class;


    @PostConstruct
    public void init() {

        bossLoop = createGroup(bossThreadSize, "boss");
        workerLoop = createGroup(workerThreadSize, "worker");
        clientLoop = createGroup(clientThreadSize, "client");

        if (Epoll.isAvailable()) {
            Client_Socket_Channel_Class = EpollSocketChannel.class;
            Server_Socket_Channel_Class = EpollServerSocketChannel.class;
        } else {
            Client_Socket_Channel_Class = NioSocketChannel.class;
            Server_Socket_Channel_Class = NioServerSocketChannel.class;
        }
        sslContext = SslContextServer.sslContext(SslProtocolType.TLSV12, "jks/wxdtest-1.8.jks", "jks/wxdtest-1.8.jks.pwd");
    }

    private EventLoopGroup createGroup(int size, String prefix) {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup(size, new ThreadNameFactory(prefix));
        } else {
            return new NioEventLoopGroup(size, new ThreadNameFactory(prefix));
        }
    }

    @Bean
    @ConditionalOnMissingBean(MessageDispatcher.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public MessageDispatcher messageDispatcher() {
        MessageDispatcher messageDispatcher = new MessageDispatcher();
        log.debug("default MessageDispatcher = {}", messageDispatcher.hashCode());
        return messageDispatcher;
    }

    @Bean
    @ConditionalOnMissingBean(ServerMessageEncode.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageEncode serverMessageEncode(MessageDispatcher messageDispatcher) {
        ServerMessageEncode encode = new ServerMessageEncode(messageDispatcher) {};
        log.debug("default ServerMessageEncode = {}", encode.hashCode());
        return encode;
    }

    @Bean
    @ConditionalOnMissingBean(ServerMessageDecode.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageDecode serverMessageDecode(MessageDispatcher messageDispatcher) {
        ServerMessageDecode decode = new ServerMessageDecode(true, messageDispatcher) {};
        log.debug("default ServerMessageDecode = {}", decode.hashCode());
        return decode;
    }

    @Bean
    @ConditionalOnMissingBean(SocketServerDeviceHandler.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public SocketServerDeviceHandler socketServerDeviceHandler() {
        SocketServerDeviceHandler deviceHandler = new SocketServerDeviceHandler();
        log.debug("default SocketServerDeviceHandler = {}", deviceHandler.hashCode());
        return deviceHandler;
    }

    @Bean
    @ConditionalOnMissingBean(ClientMessageEncode.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ClientMessageEncode clientMessageEncode(MessageDispatcher messageDispatcher) {
        ClientMessageEncode decode = new ClientMessageEncode(messageDispatcher) {};
        log.debug("default ClientMessageEncode = {}", decode.hashCode());
        return decode;
    }

    @Bean
    @ConditionalOnMissingBean(ClientMessageDecode.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ClientMessageDecode clientMessageDecode(MessageDispatcher messageDispatcher) {
        ClientMessageDecode decode = new ClientMessageDecode(true, messageDispatcher) {};
        log.debug("default ClientMessageDecode = {}", decode.hashCode());
        return decode;
    }

    @Bean
    @ConditionalOnMissingBean(SocketClientDeviceHandler.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public SocketClientDeviceHandler clientDeviceHandler() {
        SocketClientDeviceHandler deviceHandler = new SocketClientDeviceHandler();
        log.debug("default SocketClientDeviceHandler = {}", deviceHandler.hashCode());
        return deviceHandler;
    }

}
