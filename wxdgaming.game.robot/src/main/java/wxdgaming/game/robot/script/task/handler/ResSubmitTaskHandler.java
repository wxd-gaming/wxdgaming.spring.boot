package wxdgaming.game.robot.script.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.cfg.QTaskTable;
import wxdgaming.game.message.task.ResSubmitTask;
import wxdgaming.game.message.task.TaskBean;
import wxdgaming.game.robot.bean.Robot;
import wxdgaming.spring.boot.excel.store.DataRepository;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 提交任务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class ResSubmitTaskHandler {

    /** 提交任务 */
    @ProtoRequest
    public void resSubmitTask(SocketSession socketSession, ResSubmitTask req) {
        Robot robot = socketSession.bindData("robot");
        TaskBean taskBean = robot.getTasks().get(req.getTaskId());
        if (taskBean == null) {
            return;
        }

        QTaskTable taskTable = DataRepository.getIns().dataTable(QTaskTable.class);
        log.info("{} 完成任务: {}", robot, taskTable.get(taskBean.getTaskId()).getInnerTaskDetail());

        taskBean.setReward(true);
        if (req.isRemove()) {
            robot.getTasks().remove(req.getTaskId());
        }
    }

}