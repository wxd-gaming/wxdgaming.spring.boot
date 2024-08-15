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
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.threading.ThreadNameFactory;

import javax.net.ssl.SSLContext;

/**
 * 配置项
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-14 20:33
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties("server.tcp")
public class ServerBootstrapConfig implements InitPrint {

    private int tcpPort = 0;
    private int bossThreadSize = 2;
    private int workerThreadSize = 10;
    private int serverSessionIdleTime = 20;

    private SSLContext sslContext = null;

    private EventLoopGroup bossLoop;
    private EventLoopGroup workerLoop;
    /**
     * 服务监听的channel
     */
    private Class<? extends ServerChannel> Server_Socket_Channel_Class;
    private Class<? extends SocketChannel> Client_Socket_Channel_Class;


    @PostConstruct
    public void init() {

        bossLoop = createGroup(bossThreadSize, "boss");
        workerLoop = createGroup(workerThreadSize, "worker");

        if (Epoll.isAvailable()) {
            Client_Socket_Channel_Class = EpollSocketChannel.class;
            Server_Socket_Channel_Class = EpollServerSocketChannel.class;
        } else {
            Client_Socket_Channel_Class = NioSocketChannel.class;
            Server_Socket_Channel_Class = NioServerSocketChannel.class;
        }

    }

    EventLoopGroup createGroup(int size, String prefix) {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup(size, new ThreadNameFactory(prefix));
        } else {
            return new NioEventLoopGroup(size, new ThreadNameFactory(prefix));
        }
    }

}
