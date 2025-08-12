package wxdgaming.game.robot.script.role.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.role.ResChooseRole;
import wxdgaming.game.robot.bean.Robot;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 选择角色响应
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResChooseRoleHandler {

    /** 选择角色响应 */
    @ProtoRequest
    public void resChooseRole(SocketSession socketSession, ResChooseRole req) {
        Robot robot = socketSession.bindData("robot");
        robot.setLoginEnd(true);
    }

}