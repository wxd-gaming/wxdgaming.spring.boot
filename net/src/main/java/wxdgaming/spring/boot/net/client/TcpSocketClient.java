package wxdgaming.spring.boot.net.client;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import wxdgaming.spring.boot.core.threading.DefaultExecutor;
import wxdgaming.spring.boot.net.BootstrapBuilder;
import wxdgaming.spring.boot.net.SessionHandler;

/**
 * tcp socket client
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-20 19:13
 */
@Getter
public class TcpSocketClient extends SocketClient {

    public TcpSocketClient(DefaultExecutor defaultExecutor,
                           BootstrapBuilder bootstrapBuilder,
                           SocketClientBuilder socketClientBuilder,
                           SocketClientBuilder.Config config,
                           SessionHandler sessionHandler,
                           ClientMessageDecode clientMessageDecode,
                           ClientMessageEncode clientMessageEncode) {
        super(defaultExecutor, bootstrapBuilder, socketClientBuilder, config, sessionHandler, clientMessageDecode, clientMessageEncode);
    }

    @PostConstruct
    @Override public void init() {
        super.init();
    }

}
