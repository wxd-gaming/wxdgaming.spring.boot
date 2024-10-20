package wxdgaming.spring.boot.net;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
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

    public static EventLoopGroup createGroup(int size, String prefix) {
        if (Epoll.isAvailable()) {
            return new EpollEventLoopGroup(size, new WxdThreadFactory(prefix));
        } else {
            return new NioEventLoopGroup(size, new WxdThreadFactory(prefix));
        }
    }

    @Bean
    @ConditionalOnMissingBean(SessionHandler.class)
    public SessionHandler sessionHandler() {
        SessionHandler sessionHandler = new SessionHandler() {};
        log.debug("init default sessionHandler = {}", sessionHandler.hashCode());
        return sessionHandler;
    }

    @Bean
    @ConditionalOnMissingBean(MessageDispatcher.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public MessageDispatcher messageDispatcher() {
        MessageDispatcher messageDispatcher = new MessageDispatcher();
        log.debug("init default MessageDispatcher = {}", messageDispatcher.hashCode());
        return messageDispatcher;
    }


}
