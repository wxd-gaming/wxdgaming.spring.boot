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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.threading.ThreadNameFactory;
import wxdgaming.spring.boot.net.client.ClientMessageAction;
import wxdgaming.spring.boot.net.client.SocketClientDeviceHandler;
import wxdgaming.spring.boot.net.server.ServerMessageAction;
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
@ConditionalOnProperty("server.tcp")
@ConfigurationProperties("server.tcp")
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

    }

    private EventLoopGroup createGroup(int size, String prefix) {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup(size, new ThreadNameFactory(prefix));
        } else {
            return new NioEventLoopGroup(size, new ThreadNameFactory(prefix));
        }
    }

    @Bean
    @ConditionalOnMissingBean(ServerMessageAction.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageAction serverMessageAction() {

        ServerMessageAction messageAction = new ServerMessageAction() {};

        log.debug("default ServerMessageAction = {}", messageAction.hashCode(), new RuntimeException("日志"));
        return messageAction;
    }

    @Bean
    @ConditionalOnMissingBean(SocketServerDeviceHandler.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public SocketServerDeviceHandler socketServerDeviceHandler(ServerMessageAction messageAction) {
        SocketServerDeviceHandler deviceHandler = new SocketServerDeviceHandler(messageAction, true);
        log.debug("default SocketServerDeviceHandler = {}", deviceHandler.hashCode(), new RuntimeException("日志"));
        return deviceHandler;
    }

    @Bean
    @ConditionalOnMissingBean(ClientMessageAction.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ClientMessageAction clientMessageAction() {

        ClientMessageAction messageAction = new ClientMessageAction() {};
        log.debug("default ClientMessageAction = {}", messageAction.hashCode(), new RuntimeException("日志"));
        return messageAction;
    }

    @Bean
    @ConditionalOnMissingBean(SocketServerDeviceHandler.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public SocketClientDeviceHandler clientDeviceHandler(ClientMessageAction messageAction) {
        SocketClientDeviceHandler deviceHandler = new SocketClientDeviceHandler(messageAction, true);
        log.debug("default SocketClientDeviceHandler = {}", deviceHandler.hashCode(), new RuntimeException("日志"));
        return deviceHandler;
    }

}
