package wxdgaming.spring.boot.net.server;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.net.BootstrapBuilder;

/**
 * server 1
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-12 20:12
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
@Configuration
@ConfigurationProperties("socket.server1")
@ConditionalOnProperty(prefix = "socket.server1.config", name = "port")
public class SocketServer1Builder implements InitPrint {

    ServerConfig config;

    @Bean(name = "socket.server1")
    public SocketService socketService1(BootstrapBuilder bootstrapBuilder) throws Exception {
        return SocketService.createSocketService(bootstrapBuilder, config);
    }

}
