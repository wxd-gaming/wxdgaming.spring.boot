package wxdgaming.spring.boot.net.server;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.ssl.SslContextByJks;
import wxdgaming.spring.boot.core.ssl.SslContextNoFile;
import wxdgaming.spring.boot.core.ssl.SslProtocolType;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.net.BootstrapBuilder;
import wxdgaming.spring.boot.net.MessageDispatcher;
import wxdgaming.spring.boot.net.SessionGroup;
import wxdgaming.spring.boot.net.SessionHandler;

import javax.net.ssl.SSLContext;
import java.lang.reflect.Constructor;

/**
 * socket 服务器配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-06 19:48
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
@Configuration
@ConfigurationProperties("socket.server")
public class SocketServerBuilder {

    /** netty boss 线程 多个服务共享 */
    private int bossThreadSize = 3;
    /** netty work 线程 多个服务共享 */
    private int workerThreadSize = 20;

    private Config config;

    private EventLoopGroup bossLoop;
    private EventLoopGroup workerLoop;
    /** 服务监听的channel */
    private Class<? extends ServerChannel> Server_Socket_Channel_Class;

    @PostConstruct
    public void init() {
        bossLoop = BootstrapBuilder.createGroup(bossThreadSize, "boss");
        workerLoop = BootstrapBuilder.createGroup(workerThreadSize, "worker");
        if (Epoll.isAvailable()) {
            Server_Socket_Channel_Class = EpollServerSocketChannel.class;
        } else {
            Server_Socket_Channel_Class = NioServerSocketChannel.class;
        }

    }

    @Bean
    @ConditionalOnMissingBean(ServerMessageEncode.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageEncode serverMessageEncode(MessageDispatcher messageDispatcher) {
        ServerMessageEncode encode = new ServerMessageEncode(messageDispatcher) {};
        log.debug("init default ServerMessageEncode = {}", encode.hashCode());
        return encode;
    }

    @Bean
    @ConditionalOnMissingBean(ServerMessageDecode.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ServerMessageDecode serverMessageDecode(BootstrapBuilder bootstrapBuilder, MessageDispatcher messageDispatcher) {
        ServerMessageDecode decode = new ServerMessageDecode(bootstrapBuilder, messageDispatcher) {};
        log.debug("init default ServerMessageDecode = {}", decode.hashCode());
        return decode;
    }

    @Bean(name = "socketService")
    @ConditionalOnProperty(prefix = "socket.server.config", name = "port")
    public SocketService socketService(BootstrapBuilder bootstrapBuilder,
                                       ServerMessageDecode serverMessageDecode,
                                       ServerMessageEncode serverMessageEncode) throws Exception {

        if (StringsUtil.emptyOrNull(config.getServiceClass())) {
            config.setServiceClass(SocketService.class.getName());
        }

        Class aClass = Thread.currentThread().getContextClassLoader().loadClass(config.getServiceClass());
        Constructor<SocketService> declaredConstructor = aClass.getDeclaredConstructors()[0];
        return declaredConstructor.newInstance(
                bootstrapBuilder,
                this,
                config,
                serverMessageDecode,
                serverMessageEncode
        );

    }

    /** 配置 */
    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Config {

        private String serviceClass;
        private int port = 18001;
        private int idleTimeout = 30;
        /** 是否开启 ssl */
        private boolean enableSsl = false;
        /** 是否开启 web socket */
        private boolean enableWebSocket = false;
        private String webSocketPrefix = "/wxd-gaming";
        /** 默认的 ssl 类型 */
        private SslProtocolType sslProtocolType = SslProtocolType.TLSV12;
        /** jks 路径 */
        private String jks_path = "";
        /** jks 密钥 */
        private String jks_pwd_path = "";

        private SSLContext sslContext = null;

        public SSLContext getSslContext() {
            if (sslContext == null) {
                if (StringsUtil.notEmptyOrNull(jks_path)) {
                    sslContext = SslContextByJks.sslContext(sslProtocolType, jks_path, jks_pwd_path);
                } else {
                    sslContext = SslContextNoFile.sslContext(sslProtocolType);
                }
            }
            return sslContext;
        }
    }

}
