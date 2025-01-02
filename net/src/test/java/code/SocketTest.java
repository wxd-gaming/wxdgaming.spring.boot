package code;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.core.function.Consumer2;
import wxdgaming.spring.boot.core.function.Consumer3;
import wxdgaming.spring.boot.core.threading.DefaultExecutor;
import wxdgaming.spring.boot.net.*;
import wxdgaming.spring.boot.net.client.ClientConfig;
import wxdgaming.spring.boot.net.client.SocketClient;
import wxdgaming.spring.boot.net.client.SocketClientBuilder;
import wxdgaming.spring.boot.net.pojo.inner.InnerMessage;
import wxdgaming.spring.boot.net.server.ServerConfig;
import wxdgaming.spring.boot.net.server.ServerMessageDispatcher;
import wxdgaming.spring.boot.net.server.SocketServerBuilder;
import wxdgaming.spring.boot.net.server.SocketService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest(classes = {CoreScan.class, NetScan.class})
public class SocketTest {

    BootstrapBuilder bootstrapBuilder;
    ServerMessageDispatcher messageDispatcher;
    DefaultExecutor defaultExecutor;

    SocketService socketService;
    SocketClient socketClient;

    @Before
    public void before() throws Exception {
        defaultExecutor = new DefaultExecutor(2);
        bootstrapBuilder = new BootstrapBuilder();
        bootstrapBuilder.setPrintLogger(true);
        bootstrapBuilder.init();

        messageDispatcher = new ServerMessageDispatcher(true, new String[0]);
        messageDispatcher.registerMessage(InnerMessage.ReqHeart.class);
        messageDispatcher.registerMessage(InnerMessage.ResHeart.class);

        SocketServerBuilder socketServerBuilder = new SocketServerBuilder();
        socketServerBuilder.setConfig(new ServerConfig().setEnableWebSocket(true));

        Consumer3<SocketSession, Integer, byte[]> webSocketFrame = new Consumer3<SocketSession, Integer, byte[]>() {
            @Override public void accept(SocketSession socketSession, Integer integer, byte[] bytes) {
                long startTime = System.nanoTime();
                for (int i = 0; i < 10; i++) {
                    writeAndFlush(socketService.getSessionGroup(), "socket server writeAndFlush " + i);
                }
                log.info("writeAndFlush cost: {}", (System.nanoTime() - startTime) / 10000 / 100f);
            }
        };

        Consumer2<SocketSession, String> webSocketText = new Consumer2<SocketSession, String>() {
            @Override public void accept(SocketSession socketSession, String message) {
                log.info("{}", message);
                socketSession.write("socket server textWebSocketFrame");
            }
        };

        socketService = socketServerBuilder.socketService(bootstrapBuilder);
        socketService.init();
        socketService.getServerMessageDecode().getDispatcher().setMsgBytesNotDispatcher(webSocketFrame);
        socketService.getServerMessageDecode().getDispatcher().setStringDispatcher(webSocketText);
        socketService.start();

        SocketClientBuilder socketClientBuilder = new SocketClientBuilder();
        socketClientBuilder.setConfig(new ClientConfig().setPort(socketServerBuilder.getConfig().getPort()).setUseWebSocket(true));

        socketClient = socketClientBuilder.socketClient(
                defaultExecutor,
                bootstrapBuilder
        );
        socketClient.init();
    }

    @After
    public void after() throws IOException {
        socketService.close();
    }

    @Test
    public void t0() throws Exception {

        for (int i = 0; i < 1; i++) {
            socketClient.connect();
            // webSocketClient.connect();
            Thread.sleep(5);
        }
        for (int i = 0; i < 2; i++) {
            System.in.read();
            writeAndFlush(socketClient.getSessionGroup(), "tcp socket client");
        }
        // {
        //     System.in.read();
        //     SocketSession session = webSocketClient.idleSession();
        //     writeAndFlush(session, "web socket client");
        //     writeAndFlush(session, "web socket client");
        //     writeAndFlush(session, "web socket client");
        //     session.writeAndFlush("TextWebSocketFrame");
        // }
        // // Thread.sleep(3000);
        System.in.read();
    }

    public void writeAndFlush(SessionGroup sessions, String message) {
        ByteBuf byteBuf = ByteBufUtil.pooledByteBuf(30);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length + 4);
        byteBuf.writeInt(1);
        byteBuf.writeBytes(bytes);
        sessions.writeAndFlush(byteBuf);
    }

    public void write(SessionGroup channels, String message) {
        ByteBuf byteBuf = ByteBufUtil.pooledByteBuf(30);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length + 4);
        byteBuf.writeInt(1);
        byteBuf.writeBytes(bytes);
        channels.write(byteBuf);
    }

}
