package wxdgaming.spring.boot.net.client;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
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
    @Bean(name = "socketClient")
    public SocketClient socketClient(DefaultExecutor defaultExecutor, BootstrapBuilder bootstrapBuilder) {

        ClientMessageDispatcher clientMessageDispatcher = new ClientMessageDispatcher(bootstrapBuilder.isPrintLogger());
        ClientMessageDecode clientMessageDecode = new ClientMessageDecode(clientMessageDispatcher);
        ClientMessageEncode clientMessageEncode = new ClientMessageEncode(clientMessageDispatcher);

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
