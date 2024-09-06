package wxdgaming.spring.boot.net;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
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
import wxdgaming.spring.boot.core.ssl.SslContextByJks;
import wxdgaming.spring.boot.core.ssl.SslProtocolType;
import wxdgaming.spring.boot.core.threading.ThreadNameFactory;
import wxdgaming.spring.boot.net.server.SocketServerBuilder;

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
@ConfigurationProperties("socket")
public class BootstrapConfig implements InitPrint {

    private boolean debugLogger = false;

    public static EventLoopGroup createGroup(int size, String prefix) {
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
        log.debug("init default MessageDispatcher = {}", messageDispatcher.hashCode());
        return messageDispatcher;
    }


}
