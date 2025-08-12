package wxdgaming.game.robot.script.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.excel.store.DataRepository;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;
import wxdgaming.game.message.task.ResUpdateTaskList;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.robot.bean.Robot;
import wxdgaming.game.cfg.QTaskTable;

import java.util.List;

/**
 * 更新任务列表
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ResUpdateTaskListHandler {

    /** 更新任务列表 */
    @ProtoRequest
    public void resUpdateTaskList(SocketSession socketSession, ResUpdateTaskList req) {
        Robot robot = socketSession.bindData("robot");
        List<TaskBean> tasks = req.getTasks();
        QTaskTable taskTable = DataRepository.getIns().dataTable(QTaskTable.class);
        for (TaskBean task : tasks) {
            robot.getTasks().put(task.getTaskId(), task);
            log.info("{} 任务更新: {}, {}", robot, taskTable.get(task.getTaskId()).getInnerTaskDetail(), task);
        }
    }

}