package wxdgaming.spring.boot.starter.net;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.starter.net.client.SocketClientConfig;
import wxdgaming.spring.boot.starter.net.client.SocketClientImpl;
import wxdgaming.spring.boot.starter.net.server.SocketServerConfig;
import wxdgaming.spring.boot.starter.net.server.SocketServerImpl;

/**
 * 扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 08:34
 **/
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "socket")
public class NetScan {

    SocketServerConfig server;
    SocketClientConfig client;

    @Bean
    @Primary
    @ConditionalOnProperty(name = "socket.server.port")
    public SocketServerImpl initService() {
        return new SocketServerImpl(server);
    }

    @Bean
    @Primary
    @ConditionalOnProperty(name = "socket.client.port")
    public SocketClientImpl initClient() {
        return new SocketClientImpl(client);
    }

}
