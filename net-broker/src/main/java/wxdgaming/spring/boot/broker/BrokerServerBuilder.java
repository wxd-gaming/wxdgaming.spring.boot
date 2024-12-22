package wxdgaming.spring.boot.broker;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.broker.pojo.inner.InnerMessage;
import wxdgaming.spring.boot.net.*;
import wxdgaming.spring.boot.net.server.ServerConfig;
import wxdgaming.spring.boot.net.server.SocketService;

/**
 * socket 服务器配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-06 19:48
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
@Configuration
@ConfigurationProperties("socket.broker")
@ConditionalOnProperty(prefix = "socket.broker.config", name = "port")
public class BrokerServerBuilder {

    private ServerConfig config;
    private final SessionGroup sessionGroup = new SessionGroup();

    @Bean(name = "brokerService")
    public SocketService brokerService(BootstrapBuilder bootstrapBuilder, DataCenter dataCenter) throws Exception {
        SocketService socketService = SocketService.createSocketService(bootstrapBuilder, config);
        socketService.setSessionGroup(sessionGroup);
        socketService.getServerMessageDecode().setDoMessage(new DoMessage() {
            @Override public void actionString(SocketSession socketSession, String message) throws Exception {
                super.actionString(socketSession, message);
            }

            @Override public void notSpi(SocketSession socketSession, int messageId, byte[] messageBytes) {
                Integer sid = socketSession.attribute("sid");

                ServerMapping serverMapping = dataCenter.getSessions().get(InnerMessage.Stype.GAME, sid);
                if (serverMapping != null && serverMapping.getSession() != null) {
                    ByteBuf build = MessageEncode.build(messageId, messageBytes);
                    /*转发消息*/
                    serverMapping.getSession().writeAndFlush(build);
                }
            }
        });
        return socketService;
    }

}
