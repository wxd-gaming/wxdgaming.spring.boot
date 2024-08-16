package code;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import wxdgaming.spring.boot.net.BootstrapConfig;
import wxdgaming.spring.boot.net.ByteBufUtil;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.client.ClientMessageAction;
import wxdgaming.spring.boot.net.client.SocketClient;
import wxdgaming.spring.boot.net.client.SocketClientDeviceHandler;
import wxdgaming.spring.boot.net.client.WebSocketClient;
import wxdgaming.spring.boot.net.server.ServerMessageAction;
import wxdgaming.spring.boot.net.server.SocketServerDeviceHandler;
import wxdgaming.spring.boot.net.server.SocketService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SocketTest {

    BootstrapConfig bootstrapConfig;
    SocketService socketService;
    SocketClient socketClient;
    WebSocketClient webSocketClient;

    @Before
    public void before() {
        bootstrapConfig = new BootstrapConfig();
        bootstrapConfig.init();
        socketService = new SocketService(bootstrapConfig, new SocketServerDeviceHandler(new ServerMessageAction() {
            @Override public void action(SocketSession session, int messageId, byte[] messageBytes) throws Exception {
                log.info("收到消息：ctx={}, message={}", session, new String(messageBytes, StandardCharsets.UTF_8));
                send(session, "socket server");
            }

            @Override public void action(SocketSession session, String message) throws Exception {
                ServerMessageAction.super.action(session, message);
                session.writeAndFlush("socket server textWebSocketFrame");
            }

        }, true));
        socketService.init();
        socketService.start();

        socketClient = new SocketClient(bootstrapConfig, new SocketClientDeviceHandler(new ClientMessageAction() {

            @Override public void action(SocketSession session, int messageId, byte[] messageBytes) throws Exception {
                log.info("收到消息：ctx={}, message={}", session, new String(messageBytes, StandardCharsets.UTF_8));
            }

        }, true));
        socketClient.setHost("127.0.0.1");
        socketClient.setPort(bootstrapConfig.getTcpPort());
        socketClient.init();

        webSocketClient = new WebSocketClient(bootstrapConfig, new SocketClientDeviceHandler(new ClientMessageAction() {

            @Override public void action(SocketSession session, int messageId, byte[] messageBytes) throws Exception {
                log.info("收到消息：ctx={}, message={}", session, new String(messageBytes, StandardCharsets.UTF_8));
            }

        }, true));
        webSocketClient.setHost("127.0.0.1");
        webSocketClient.setPort(bootstrapConfig.getTcpPort());
        webSocketClient.init();
    }

    @After
    public void after() throws IOException {
        socketService.close();
    }

    @Test
    public void t0() throws Exception {

        {
            SocketSession session = socketClient.connect();
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
