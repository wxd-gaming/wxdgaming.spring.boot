package wxdgaming.game.robot.script.chat.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.chat.ResChatMessage;
import wxdgaming.game.robot.bean.Robot;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 聊天响应
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResChatMessageHandler {

    /** 聊天响应 */
    @ProtoRequest
    public void resChatMessage(SocketSession socketSession, ResChatMessage req) {
        Robot robot = socketSession.bindData("robot");
        log.info("{} 收到聊天响应：{}", robot, req);
    }

}