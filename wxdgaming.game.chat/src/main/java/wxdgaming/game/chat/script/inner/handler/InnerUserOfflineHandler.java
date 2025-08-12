package wxdgaming.game.chat.script.inner.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.inner.InnerUserOffline;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 玩家离线
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class InnerUserOfflineHandler {

    /** 玩家离线 */
    @ProtoRequest
    public void innerUserOffline(SocketSession socketSession, InnerUserOffline req) {
        
    }

}