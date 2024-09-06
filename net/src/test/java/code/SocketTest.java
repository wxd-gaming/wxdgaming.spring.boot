package code;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import wxdgaming.spring.boot.net.BootstrapConfig;
import wxdgaming.spring.boot.net.ByteBufUtil;
import wxdgaming.spring.boot.net.MessageDispatcher;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.client.*;
import wxdgaming.spring.boot.net.server.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SocketTest {

    BootstrapConfig bootstrapConfig;
    SocketService socketService;
    TcpSocketClient tcpSocketClient;
    WebSocketClient webSocketClient;

    @Before
    public void before() {
        MessageDispatcher messageDispatcher = new MessageDispatcher();
        messageDispatcher.registerMessage(Thread.currentThread().getContextClassLoader(), "a");
        bootstrapConfig = new BootstrapConfig();
        SocketServerBuilder socketServerBuilder = new SocketServerBuilder();
        socketServerBuilder.init();
        socketServerBuilder.setConfig(new SocketServerBuilder.Config());
        socketService = socketServerBuilder.socketService(
                bootstrapConfig,
                new SocketServerDeviceHandler(),
                new ServerMessageDecode(messageDispatcher) {
                    @Override public void action(SocketSession session, int messageId, byte[] messageBytes) throws Exception {
                        log.info("收到消息：ctx={}, message={}", session, new String(messageBytes, StandardCharsets.UTF_8));
                        send(session, "socket server");
                    }

                    @Override public void action(SocketSession session, String message) throws Exception {
                        super.action(session, message);
                        session.writeAndFlush("socket server textWebSocketFrame");
                    }

                },
                new ServerMessageEncode(messageDispatcher)
        );
        socketService.init();
        socketService.start();

        SocketClientBuilder socketClientBuilder = new SocketClientBuilder();
        socketClientBuilder.setTcp(new SocketClientBuilder.Config().setPort(socketServerBuilder.getConfig().getPort()));
        socketClientBuilder.setWeb(new SocketClientBuilder.Config().setPort(socketServerBuilder.getConfig().getPort()));
        socketClientBuilder.init();

        tcpSocketClient = socketClientBuilder.tcpSocketClient(
                bootstrapConfig,
                new SocketClientDeviceHandler(),
                new ClientMessageDecode(messageDispatcher) {

                    @Override public void action(SocketSession session, int messageId, byte[] messageBytes) throws Exception {
                        log.info("收到消息：ctx={}, message={}", session, new String(messageBytes, StandardCharsets.UTF_8));
                    }

                },
                new ClientMessageEncode(messageDispatcher)
        );
        tcpSocketClient.init();

        webSocketClient = socketClientBuilder.webSocketClient(
                bootstrapConfig,
                new SocketClientDeviceHandler(),
                new ClientMessageDecode(messageDispatcher) {

                    @Override public void action(SocketSession session, int messageId, byte[] messageBytes) throws Exception {
                        log.info("收到消息：ctx={}, message={}", session, new String(messageBytes, StandardCharsets.UTF_8));
                    }

                },
                new ClientMessageEncode(messageDispatcher)
        );

        webSocketClient.init();

    }

    @After
    public void after() throws IOException {
        socketService.close();
    }

    @Test
    public void t0() throws Exception {

        {
            SocketSession session = tcpSocketClient.connect();
            send(session, "tcp socket client");
        }
        {
            SocketSession session = webSocketClient.connect();
            System.in.read();
            send(session, "web socket client");
            session.writeAndFlush("TextWebSocketFrame");

        }
        // Thread.sleep(3000);
        System.in.read();
    }

    public void send(SocketSession session, String message) throws IOException {
        ByteBuf byteBuf = ByteBufUtil.pooledByteBuf(30);
        byte[] bytes = message.getBytes(StandardCharsets.UTF_8);
        byteBuf.writeInt(bytes.length + 4);
        byteBuf.writeInt(1);
        byteBuf.writeBytes(bytes);
        session.writeAndFlush(byteBuf);
    }

}
