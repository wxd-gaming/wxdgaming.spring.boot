package wxdgaming.spring.boot.net.client;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.core.annotation.Order;
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
public class TcpSocketClient extends SocketClient {

    public TcpSocketClient(BootstrapConfig bootstrapConfig,
                           SocketClientBuilder socketClientBuilder,
                           SocketClientBuilder.Config config,
                           SocketClientDeviceHandler socketClientDeviceHandler,
                           ClientMessageDecode clientMessageDecode,
                           ClientMessageEncode clientMessageEncode) {
        super(bootstrapConfig, socketClientBuilder, config, socketClientDeviceHandler, clientMessageDecode, clientMessageEncode);
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
