package code;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import wxdgaming.spring.boot.core.threading.DefaultExecutor;
import wxdgaming.spring.boot.net.BootstrapBuilder;
import wxdgaming.spring.boot.net.NetScan;
import wxdgaming.spring.boot.net.client.ClientConfig;
import wxdgaming.spring.boot.net.client.SocketClient;
import wxdgaming.spring.boot.net.client.SocketClientBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SocketClientTest {

    BootstrapBuilder bootstrapBuilder;
    DefaultExecutor defaultExecutor;

    SocketClient socketClient;

    @Before
    public void before() throws Exception {
        defaultExecutor = new DefaultExecutor(2);
        bootstrapBuilder = new BootstrapBuilder();
        bootstrapBuilder.setPrintLogger(true);
        bootstrapBuilder.init();


        SocketClientBuilder socketClientBuilder = new SocketClientBuilder();
        socketClientBuilder.setConfig(new ClientConfig()
                .setIdleTimeout(0)
                .setHost("www.baidu.com")
                .setPort(443)
                .setUseWebSocket(false)
                .setScanHandlers(new String[]{NetScan.class.getPackageName()})
        );

        socketClient = socketClientBuilder.socketClient(
                defaultExecutor,
                bootstrapBuilder
        );
        socketClient.init();
    }

    @After
    public void after() throws IOException {
    }

    @Test
    public void t0() throws Exception {

        // socketClient.getConfig().setHost("www.csdn.net").setPort(80);
        socketClient.getConfig().setHost("www.baidu.com").setPort(80);
        for (int i = 0; i < 1; i++) {
            socketClient.connect(session -> {
                // session.writeAndFlush(build("GET / HTTP/1.1\n"));
                // session.writeAndFlush(build("User-Agent curl/7.81.0\n"));
                // session.writeAndFlush(build("Accept */*\n"));
            });
        }

        Thread.sleep(10000);
        socketClient.getSessionGroup().writeAndFlush(build("GET / HTTP/1.1\n"));
        Thread.sleep(20000);
        socketClient.getSessionGroup().writeAndFlush(build("User-Agent: curl/7.81.0\n"));
        Thread.sleep(30000);
        socketClient.getSessionGroup().writeAndFlush(build("Accept: */*\n"));

        System.in.read();
    }

    public byte[] build(String message) {
        System.out.println(message);
        return message.getBytes(StandardCharsets.UTF_8);
    }

}
