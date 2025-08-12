package wxdgaming.game.chat.script.inner.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.inner.InnerForwardMessage;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 请求转发消息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class InnerForwardMessageHandler {

    /** 请求转发消息 */
    @ProtoRequest
    public void innerForwardMessage(SocketSession socketSession, InnerForwardMessage req) {
        
    }

}