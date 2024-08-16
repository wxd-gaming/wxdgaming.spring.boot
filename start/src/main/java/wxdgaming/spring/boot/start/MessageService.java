package wxdgaming.spring.boot.start;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.net.ChannelUtil;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.server.ServerMessageAction;

import java.io.IOException;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-15 11:12
 **/
@Slf4j
@Service
public class MessageService implements InitPrint {

    @Bean
    @Primary
    public ServerMessageAction messageAction() {

        ServerMessageAction serverMessageAction = new ServerMessageAction() {

            @Override public void action(SocketSession ctx, int messageId, byte[] messageBytes) throws IOException {
                log.debug("收到消息：ctx={}, id={}, bytes len={}", ctx, messageId, messageBytes.length);
            }

            @Override public void action(SocketSession ctx, String message) throws IOException {
                log.debug("收到消息：ctx={}, message={}", ctx, message);
            }

        };
        log.debug("messageAction = {}", serverMessageAction.hashCode());
        return serverMessageAction;

    }
}
