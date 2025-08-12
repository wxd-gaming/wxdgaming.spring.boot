package wxdgaming.game.robot.script.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.cfg.QTaskTable;
import wxdgaming.game.message.task.ResAcceptTask;
import wxdgaming.game.robot.bean.Robot;
import wxdgaming.spring.boot.excel.store.DataRepository;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 接受任务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class ResAcceptTaskHandler {

    /** 接受任务 */
    @ProtoRequest
    public void resAcceptTask(SocketSession socketSession, ResAcceptTask req) {
        Robot robot = socketSession.bindData("robot");

        QTaskTable taskTable = DataRepository.getIns().dataTable(QTaskTable.class);
        log.info("{} 接取任务: {}", robot, taskTable.get(req.getTaskId()).getInnerTaskDetail());

        robot.getTasks().put(req.getTaskId(), req.getTask());
    }

}