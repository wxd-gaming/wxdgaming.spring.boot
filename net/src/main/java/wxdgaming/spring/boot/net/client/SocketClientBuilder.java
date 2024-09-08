package wxdgaming.spring.boot.net.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
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
import wxdgaming.spring.boot.core.threading.DefaultExecutor;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.net.BootstrapBuilder;
import wxdgaming.spring.boot.net.MessageDispatcher;
import wxdgaming.spring.boot.net.SessionHandler;

import javax.net.ssl.SSLContext;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-20 17:48
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
@Configuration
@ConfigurationProperties("socket.client")
public class SocketClientBuilder {

    private int clientThreadSize = 2;
    private Config tcp;
    private Config web;

    private EventLoopGroup clientLoop;
    private Class<? extends SocketChannel> Client_Socket_Channel_Class;

    @PostConstruct
    public void init() {
        clientLoop = BootstrapBuilder.createGroup(clientThreadSize, "client");

        if (Epoll.isAvailable()) {
            Client_Socket_Channel_Class = EpollSocketChannel.class;
        } else {
            Client_Socket_Channel_Class = NioSocketChannel.class;
        }
    }

    @Bean
    @ConditionalOnMissingBean(ClientMessageEncode.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ClientMessageEncode clientMessageEncode(MessageDispatcher messageDispatcher) {
        ClientMessageEncode decode = new ClientMessageEncode(messageDispatcher) {};
        log.debug("init default ClientMessageEncode = {}", decode.hashCode());
        return decode;
    }

    @Bean
    @ConditionalOnMissingBean(ClientMessageDecode.class)/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ClientMessageDecode clientMessageDecode(BootstrapBuilder bootstrapBuilder, MessageDispatcher messageDispatcher) {
        ClientMessageDecode decode = new ClientMessageDecode(bootstrapBuilder, messageDispatcher) {};
        log.debug("init default ClientMessageDecode = {}", decode.hashCode());
        return decode;
    }

    @Bean()
    @ConditionalOnProperty(prefix = "socket.client.tcp", name = "port")
    public TcpSocketClient tcpSocketClient(DefaultExecutor defaultExecutor, BootstrapBuilder bootstrapBuilder,
                                           SessionHandler sessionHandler,
                                           ClientMessageDecode clientMessageDecode,
                                           ClientMessageEncode clientMessageEncode) {
        return new TcpSocketClient(
                defaultExecutor,
                bootstrapBuilder,
                this,
                tcp, sessionHandler,
                clientMessageDecode,
                clientMessageEncode
        );
    }

    @Bean()
    @ConditionalOnProperty(prefix = "socket.client.web", name = "port")
    public WebSocketClient webSocketClient(DefaultExecutor defaultExecutor, BootstrapBuilder bootstrapBuilder,
                                           SessionHandler sessionHandler,
                                           ClientMessageDecode clientMessageDecode,
                                           ClientMessageEncode clientMessageEncode) {
        return new WebSocketClient(
                defaultExecutor,
                bootstrapBuilder,
                this,
                web, sessionHandler,
                clientMessageDecode,
                clientMessageEncode
        );
    }


    @Getter
    @Setter
    @Accessors(chain = true)
    public static class Config {

        protected String host = "127.0.0.1";
        private int port = 18001;
        private int idleTimeout = 30;
        private int connectTimeout = 2000;
        private boolean enableSsl = false;
        private String prefix = "/wxd-gaming";
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
