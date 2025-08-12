package wxdgaming.game.gateway.module.service;

import org.springframework.boot.context.properties.ConfigurationProperties;
import wxdgaming.spring.boot.net.client.SocketClientConfig;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 20:30
 **/
@ConfigurationProperties(prefix = "socket.client-forward")
public class ClientForwardConfig extends SocketClientConfig {
}
