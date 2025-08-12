package wxdgaming.spring.boot.net;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.net.client.SocketClient;
import wxdgaming.spring.boot.net.server.SocketServer;

/**
 * socket 模块
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 09:45
 **/
@Getter
@ComponentScan
@Component
@EnableConfigurationProperties(SocketProperties.class)
public class SocketConfiguration implements InitPrint {

    private final CoreConfiguration coreConfiguration;
    private final SocketProperties socketProperties;

    @Autowired
    public SocketConfiguration(CoreConfiguration coreConfiguration, SocketProperties socketProperties) {
        this.coreConfiguration = coreConfiguration;
        this.socketProperties = socketProperties;
    }

    @Bean
    @ConditionalOnProperty(name = "socket.client.port")
    public SocketClient socketClient() {
        if (socketProperties.getClient().isEnabledWebSocket()) {
            if (StringUtils.isBlank(socketProperties.getClient().getWebSocketPrefix())) {
                throw new RuntimeException("WebSocket 模块配置错误，WebSocket 模块需要配置 WebSocket 前缀");
            }
        }
        return new SocketClient(socketProperties.getClient());
    }

    @Bean
    @ConditionalOnProperty(name = "socket.server.port")
    public SocketServer socketServer() {
        if (socketProperties.getServer().isEnabledWebSocket()) {
            if (StringUtils.isBlank(socketProperties.getServer().getWebSocketPrefix())) {
                throw new RuntimeException("WebSocket 模块配置错误，WebSocket 模块需要配置 WebSocket 前缀");
            }
        }
        return new SocketServer(socketProperties.getServer());
    }

    @Bean("serverSecond")
    @ConditionalOnProperty(name = "socket.server-second.port")
    public SocketServer serverSecond() {
        if (socketProperties.getServerSecond().isEnabledWebSocket()) {
            if (StringUtils.isBlank(socketProperties.getServerSecond().getWebSocketPrefix())) {
                throw new RuntimeException("WebSocket 模块配置错误，WebSocket 模块需要配置 WebSocket 前缀");
            }
        }
        return new SocketServer(socketProperties.getServerSecond());
    }

}
