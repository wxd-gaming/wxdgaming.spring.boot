package wxdgaming.game.server.bean.task;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.core.collection.Table;
import wxdgaming.spring.boot.core.lang.ObjectBase;
import wxdgaming.game.message.task.TaskType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 任务管理器 包装
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-15 17:03
 **/
@Getter
@Setter
public class TaskPack extends ObjectBase {

    /** 任务完成ID key:任务类型, value: 完成的id集合 */
    @JSONField(ordinal = 11)
    private HashMap<TaskType, ArrayList<Integer>> taskFinishList = new HashMap<>();
    /** key:任务类型, value: 任务列表 */
    @JSONField(ordinal = 12)
    private Table<TaskType, Integer, TaskInfo> tasks = new Table<>();

    public void addFinishTask(TaskType taskType, int taskId) {
        taskFinishList.computeIfAbsent(taskType, l -> new ArrayList<>()).add(taskId);
    }

}
