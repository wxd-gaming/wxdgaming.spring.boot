package wxdgaming.spring.boot.net.client;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wxdgaming.spring.boot.core.threading.DefaultExecutor;
import wxdgaming.spring.boot.net.BootstrapBuilder;

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
@ConditionalOnProperty(prefix = "socket.client.config", name = "port")
public class SocketClientBuilder {

    private ClientConfig config;

    @Primary
    @Bean(name = "clientMessageDispatcher")
    @ConditionalOnMissingBean(name = "clientMessageDispatcher")/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ClientMessageDispatcher clientMessageDispatcher() {
        ClientMessageDispatcher messageDispatcher = new ClientMessageDispatcher(config.getScanPkgs());
        log.debug("init default clientMessageDispatcher = {}", messageDispatcher.hashCode());
        return messageDispatcher;
    }

    @Bean
    @ConditionalOnMissingBean(name = "clientMessageEncode")/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ClientMessageEncode clientMessageEncode(@Qualifier("clientMessageDispatcher") ClientMessageDispatcher clientMessageDispatcher) {
        ClientMessageEncode decode = new ClientMessageEncode(clientMessageDispatcher) {};
        log.debug("init default ClientMessageEncode = {}", decode.hashCode());
        return decode;
    }

    @Bean
    @ConditionalOnMissingBean(name = "clientMessageDecode")/*通过扫描器检查，当不存在处理器的时候初始化默认处理器*/
    public ClientMessageDecode clientMessageDecode(BootstrapBuilder bootstrapBuilder,
                                                   @Qualifier("clientMessageDispatcher") ClientMessageDispatcher clientMessageDispatcher) {
        ClientMessageDecode decode = new ClientMessageDecode(bootstrapBuilder, clientMessageDispatcher) {};
        log.debug("init default ClientMessageDecode = {}", decode.hashCode());
        return decode;
    }

    @Primary
    @Bean(name = "socketClient")
    public SocketClient socketClient(DefaultExecutor defaultExecutor,
                                     BootstrapBuilder bootstrapBuilder,
                                     @Qualifier("clientMessageDecode") ClientMessageDecode clientMessageDecode,
                                     @Qualifier("clientMessageEncode") ClientMessageEncode clientMessageEncode) {
        if (config.isUseWebSocket()) {
            return new WebSocketClient(
                    defaultExecutor,
                    bootstrapBuilder,
                    config,
                    clientMessageDecode,
                    clientMessageEncode
            );
        }

        return new TcpSocketClient(
                defaultExecutor,
                bootstrapBuilder,
                config,
                clientMessageDecode,
                clientMessageEncode
        );
    }

}
