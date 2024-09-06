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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.ssl.SslContextByJks;
import wxdgaming.spring.boot.core.ssl.SslContextNoFile;
import wxdgaming.spring.boot.core.ssl.SslProtocolType;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.net.BootstrapConfig;

import javax.net.ssl.SSLContext;

/**
 * socket 服务器配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-06 19:48
 **/
@Getter
@Setter
@Accessors(chain = true)
@Configuration
@ConfigurationProperties("socket.server")
public class SocketServerBuilder {

    /** netty boss 线程 多个服务共享 */
    private int bossThreadSize = 2;
    /** netty work 线程 多个服务共享 */
    private int workerThreadSize = 10;

    private Config config;

    private EventLoopGroup bossLoop;
    private EventLoopGroup workerLoop;
    /** 服务监听的channel */
    private Class<? extends ServerChannel> Server_Socket_Channel_Class;

    @PostConstruct
    public void init() {
        bossLoop = BootstrapConfig.createGroup(bossThreadSize, "boss");
        workerLoop = BootstrapConfig.createGroup(workerThreadSize, "worker");
        if (Epoll.isAvailable()) {
            Server_Socket_Channel_Class = EpollServerSocketChannel.class;
        } else {
            Server_Socket_Channel_Class = NioServerSocketChannel.class;
        }

    }

    @Bean(name = "socketService")
    @ConditionalOnProperty(prefix = "socket.server.config", name = "port")
    public SocketService socketService(BootstrapConfig bootstrapConfig,
                                       SocketServerDeviceHandler socketServerDeviceHandler,
                                       ServerMessageDecode serverMessageDecode,
                                       ServerMessageEncode serverMessageEncode) {
        return new SocketService(
                bootstrapConfig,
                this,
                config,
                socketServerDeviceHandler,
                serverMessageDecode,
                serverMessageEncode
        );

    }


    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Config {

        private int port = 18001;
        private int idleTimeout = 30;
        private boolean enableSsl = false;
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
