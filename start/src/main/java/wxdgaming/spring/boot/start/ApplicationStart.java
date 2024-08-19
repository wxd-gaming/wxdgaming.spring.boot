package wxdgaming.spring.boot.start;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.data.batis.DataBatisScan;
import wxdgaming.spring.boot.data.excel.DataExcelScan;
import wxdgaming.spring.boot.data.redis.DataRedisScan;
import wxdgaming.spring.boot.message.SerializerUtil;
import wxdgaming.spring.boot.net.*;
import wxdgaming.spring.boot.net.client.ClientMessageAction;
import wxdgaming.spring.boot.net.client.SocketClient;
import wxdgaming.spring.boot.net.client.SocketClientDeviceHandler;
import wxdgaming.spring.boot.rpc.RpcScan;
import wxdgaming.spring.boot.rpc.pojo.RpcMessage;
import wxdgaming.spring.boot.web.WebScan;
import wxdgaming.spring.boot.weblua.WebLuaScan;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 启动器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 19:54
 **/
@Slf4j
@SpringBootApplication(
        scanBasePackageClasses = {
                ApplicationStart.class,
                CoreScan.class,
                DataBatisScan.class,
                DataRedisScan.class,
                DataExcelScan.class,
                WebScan.class,
                WebLuaScan.class,
                NetScan.class,
                RpcScan.class,
        },
        exclude = {
                DataSourceAutoConfiguration.class,
                MongoAutoConfiguration.class
        }
)
public class ApplicationStart {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ApplicationStart.class, args);

        SpringUtil ins = SpringUtil.getIns();
        ins.withMethodAnnotated(Start.class)
                .forEach(method -> {
                    try {
                        Object bean = ins.getBean(method.getDeclaringClass());
                        method.setAccessible(true);
                        Object[] array = Arrays.stream(method.getParameterTypes()).map(ins::getBean).toArray();
                        method.invoke(bean, array);
                    } catch (Exception e) {
                        throw new RuntimeException(method.toString(), e);
                    }
                });

        BootstrapConfig bootstrapConfig = run.getBean(BootstrapConfig.class);
        MessageDispatcher messageDispatcher = run.getBean(MessageDispatcher.class);
        SocketClient socketClient = new SocketClient(bootstrapConfig, new SocketClientDeviceHandler(new ClientMessageAction(messageDispatcher) {

            @Override public void action(SocketSession session, int messageId, byte[] messageBytes) throws Exception {
                log.info("收到消息：ctx={}, message={}", session, new String(messageBytes, StandardCharsets.UTF_8));
            }

        }, true));
        socketClient.setHost("127.0.0.1");
        socketClient.setPort(bootstrapConfig.getTcpPort());
        socketClient.init();

        SocketSession socketSession = socketClient.connect();
        RpcMessage.ReqRemote rpcMessage = new RpcMessage.ReqRemote();
        rpcMessage.setRpcId(1)
                .setCmd("a")
                .setParams("{}")
                .setRpcToken("ffffffffffffffffffffffffffffff");

        Integer msgId = messageDispatcher.getMessageName2Id().get(rpcMessage.getClass().getName());
        byte[] encode = SerializerUtil.encode(rpcMessage);
        ByteBuf byteBuf = ByteBufUtil.pooledByteBuf(50);
        byteBuf.writeInt(encode.length + 4);
        byteBuf.writeInt(msgId);
        byteBuf.writeBytes(encode);
        socketSession.writeAndFlush(byteBuf);

    }

}
