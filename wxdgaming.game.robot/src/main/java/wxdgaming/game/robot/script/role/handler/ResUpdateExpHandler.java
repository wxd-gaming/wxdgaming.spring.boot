package wxdgaming.game.robot.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.role.ResUpdateExp;
import wxdgaming.game.robot.bean.Robot;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 更新经验
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUpdateExpHandler {

    /** 更新经验 */
    @ProtoRequest
    public void resUpdateExp(SocketSession socketSession, ResUpdateExp req) {
        Robot robot = socketSession.bindData("robot");
        robot.setExp(req.getExp());
        log.info("{} 更新经验:{}", robot, req);
    }

}