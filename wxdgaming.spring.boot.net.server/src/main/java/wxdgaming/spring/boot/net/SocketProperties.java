package wxdgaming.spring.boot.net;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.net.client.SocketClientConfig;
import wxdgaming.spring.boot.net.server.SocketServerConfig;

/**
 * socket 模块
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 09:45
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "socket")
public class SocketProperties implements InitPrint {

    private SocketClientConfig client;
    private SocketServerConfig server;
    private SocketServerConfig serverSecond;

    public SocketServerConfig getServer() {
        if (server == null)
            server = new SocketServerConfig();
        return server;
    }

}
