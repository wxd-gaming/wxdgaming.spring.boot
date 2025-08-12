package wxdgaming.game.robot.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.role.ResHeartbeat;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * null
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResHeartbeatHandler {

    /** null */
    @ProtoRequest
    public void resHeartbeat(SocketSession socketSession, ResHeartbeat req) {
        Object robot = socketSession.bindData("robot");
        if (log.isDebugEnabled()) {
            log.debug("{} 心跳成功 {}", robot, req.getTimestamp());
        }
    }

}