package wxdgaming.spring.boot.net.server;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wxdgaming.spring.boot.net.BootstrapBuilder;

/**
 * socket 服务器配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-06 19:48
 **/
@Setter
@Slf4j
@Getter
@Accessors(chain = true)
@Configuration
@ConfigurationProperties("socket.server")
@ConditionalOnProperty(prefix = "socket.server.config", name = "port")
public class SocketServerBuilder {

    private ServerConfig config;

    @Primary
    @Bean(name = "socket.server")
    public SocketService socketService(BootstrapBuilder bootstrapBuilder) throws Exception {
        return SocketService.createSocketService(bootstrapBuilder, config);
    }


}
