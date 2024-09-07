package code;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import wxdgaming.spring.boot.core.threading.DefaultExecutor;
import wxdgaming.spring.boot.core.threading.ExecutorBuilder;
import wxdgaming.spring.boot.net.*;
import wxdgaming.spring.boot.net.client.*;
import wxdgaming.spring.boot.net.server.ServerMessageDecode;
import wxdgaming.spring.boot.net.server.ServerMessageEncode;
import wxdgaming.spring.boot.net.server.SocketServerBuilder;
import wxdgaming.spring.boot.net.server.SocketService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
public class SocketTest {

    BootstrapBuilder bootstrapBuilder;
    MessageDispatcher messageDispatcher;
    DefaultExecutor defaultExecutor;

    SocketService socketService;
    TcpSocketClient tcpSocketClient;
    WebSocketClient webSocketClient;

    @Before
    public void before() {
        defaultExecutor = new ExecutorBuilder().defaultExecutor();
        bootstrapBuilder = new BootstrapBuilder();
        messageDispatcher = new MessageDispatcher();

        SocketServerBuilder socketServerBuilder = new SocketServerBuilder();
        socketServerBuilder.init();
        socketServerBuilder.setConfig(new SocketServerBuilder.Config().setEnableWebSocket(true));
        socketService = socketServerBuilder.socketService(
                bootstrapBuilder,
                new SessionHandler() {},
                new ServerMessageDecode(messageDispatcher) {
                    @Override public void action(SocketSession session, int messageId, byte[] messageBytes) throws Exception {
                        super.action(session, messageId, messageBytes);
                        log.info("{}", new String(messageBytes, StandardCharsets.UTF_8));
                        send(session, "socket server");
                    }

                    @Override public void action(SocketSession session, String message) throws Exception {
                        super.action(session, message);
                        log.info("{}", message);
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
                defaultExecutor,
                bootstrapBuilder,
                new SessionHandler() {},
                new ClientMessageDecode(messageDispatcher) {
                    @Override protected void action(SocketSession socketSession, int messageId, byte[] messageBytes) throws Exception {
                        super.action(socketSession, messageId, messageBytes);
                        log.info("{}", new String(messageBytes, StandardCharsets.UTF_8));
                    }
                },
                new ClientMessageEncode(messageDispatcher)
        );
        tcpSocketClient.init();

        webSocketClient = socketClientBuilder.webSocketClient(
                defaultExecutor,
                bootstrapBuilder,
                new SessionHandler() {},
                new ClientMessageDecode(messageDispatcher),
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
