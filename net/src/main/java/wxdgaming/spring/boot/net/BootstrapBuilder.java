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
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.threading.WxdThreadFactory;

/**
 * 配置项
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-14 20:33
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
@Configuration
@ConfigurationProperties("socket")
public class BootstrapBuilder implements InitPrint {

    private boolean debugLogger = false;
    /** 输出一些基本日志 */
    private boolean printLogger = false;
    /** netty boss 线程 多个服务共享 */
    private int bossThreadSize = 3;
    /** netty work 线程 多个服务共享 */
    private int workerThreadSize = 20;
    /** netty client work 线程数量 */
    private int clientWorkSize = 2;

    private EventLoopGroup bossLoop;
    private EventLoopGroup workerLoop;
    /** 服务监听的channel */
    private Class<? extends ServerChannel> Server_Socket_Channel_Class;

    private EventLoopGroup clientLoop;
    private Class<? extends SocketChannel> Client_Socket_Channel_Class;

    @PostConstruct
    public void init() {
        bossLoop = createGroup(getBossThreadSize(), "boss");
        workerLoop = createGroup(getWorkerThreadSize(), "worker");
        clientLoop = BootstrapBuilder.createGroup(clientWorkSize, "client");
        if (Epoll.isAvailable()) {
            Server_Socket_Channel_Class = EpollServerSocketChannel.class;
            Client_Socket_Channel_Class = EpollSocketChannel.class;
        } else {
            Server_Socket_Channel_Class = NioServerSocketChannel.class;
            Client_Socket_Channel_Class = NioSocketChannel.class;
        }
    }

    public static EventLoopGroup createGroup(int size, String prefix) {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup(size, new WxdThreadFactory(prefix));
        } else {
            return new NioEventLoopGroup(size, new WxdThreadFactory(prefix));
        }
    }

}
