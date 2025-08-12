package wxdgaming.game.server.script.task.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.ann.ThreadParam;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;
import wxdgaming.game.message.task.ReqAcceptTask;
import wxdgaming.game.message.task.TaskType;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.task.TaskPack;
import wxdgaming.game.server.script.task.ITaskScript;
import wxdgaming.game.server.script.task.TaskService;

/**
 * 接受任务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version v1.1
 **/
@Slf4j
@Component
public class ReqAcceptTaskHandler {

    TaskService taskService;

    public ReqAcceptTaskHandler(TaskService taskService) {
        this.taskService = taskService;
    }

    /** 接受任务 */
    @ProtoRequest
    public void reqAcceptTask(SocketSession socketSession, ReqAcceptTask req,
                              @ThreadParam(path = "player") Player player) {
        TaskType taskType = req.getTaskType();
        int taskId = req.getTaskId();
        ITaskScript taskScript = taskService.getTaskScript(taskType);
        TaskPack taskPack = player.getTaskPack();
        taskScript.acceptTask(player, taskPack, taskId);
    }

}