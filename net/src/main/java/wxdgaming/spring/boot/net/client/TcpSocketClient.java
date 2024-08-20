package wxdgaming.spring.boot.net.client;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.net.BootstrapConfig;
import wxdgaming.spring.boot.net.SocketSession;

/**
 * tcp socket client
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-20 19:13
 */
@Getter
@Service
@ConfigurationProperties("client.tcp-socket")
@ConditionalOnProperty(prefix = "client.tcp-socket.config", name = "host")
public class TcpSocketClient extends SocketClient {

    @Autowired
    public TcpSocketClient(BootstrapConfig bootstrapConfig,
                           SocketClientDeviceHandler socketClientDeviceHandler,
                           ClientMessageDecode clientMessageDecode,
                           ClientMessageEncode clientMessageEncode) {
        super(bootstrapConfig, socketClientDeviceHandler, clientMessageDecode, clientMessageEncode);
    }

    @PostConstruct
    @Override public void init() {
        super.init();
    }

    SocketSession session;

    @Start
    @Order(2000)
    public void start() {
        session = connect();
    }

}
