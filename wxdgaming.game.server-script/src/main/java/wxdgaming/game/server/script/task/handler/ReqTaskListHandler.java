package wxdgaming.game.server.script.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.ann.ThreadParam;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;
import wxdgaming.game.message.task.ReqTaskList;
import wxdgaming.game.server.bean.role.Player;

/**
 * 任务列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqTaskListHandler {

    /** 任务列表 */
    @ProtoRequest
    public void reqTaskList(SocketSession socketSession, ReqTaskList req, @ThreadParam(path = "player") Player player) {
        
    }

}