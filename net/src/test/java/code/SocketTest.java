package code;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import wxdgaming.spring.boot.core.threading.DefaultExecutor;
import wxdgaming.spring.boot.core.threading.ExecutorBuilder;
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
public class SocketTest {

    BootstrapBuilder bootstrapBuilder;
    ServerMessageDispatcher messageDispatcher;
    DefaultExecutor defaultExecutor;

    SocketService socketService;
    SocketClient socketClient;

    @Before
    public void before() throws Exception {
        defaultExecutor = new ExecutorBuilder().defaultExecutor();
        bootstrapBuilder = new BootstrapBuilder();
        bootstrapBuilder.setPrintLogger(true);
        bootstrapBuilder.init();

        messageDispatcher = new ServerMessageDispatcher(new String[0]);
        messageDispatcher.registerMessage(InnerMessage.ReqHeart.class);
        messageDispatcher.registerMessage(InnerMessage.ResHeart.class);

        SocketServerBuilder socketServerBuilder = new SocketServerBuilder();
        socketServerBuilder.setConfig(new ServerConfig().setEnableWebSocket(true));

        DoMessage webSocketFrame = new DoMessage() {

            @Override public void notSpi(SocketSession socketSession, int messageId, byte[] messageBytes) {
                {
                    long startTime = System.nanoTime();
                    for (int i = 0; i < 10; i++) {
                        writeAndFlush(socketService.getSessionGroup(), "socket server writeAndFlush " + i);
                    }
                    log.info("writeAndFlush cost: {}", (System.nanoTime() - startTime) / 10000 / 100f);
                }
            }

            @Override public void actionString(SocketSession socketSession, String message) throws Exception {
                log.info("{}", message);
                socketSession.write("socket server textWebSocketFrame");
            }
        };

        socketService = socketServerBuilder.socketService(bootstrapBuilder);
        socketService.init();
        socketService.getServerMessageDecode().setDoMessage(webSocketFrame);
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
