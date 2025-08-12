package wxdgaming.game.server.script.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.collection.ListOf;
import wxdgaming.spring.boot.core.lang.condition.Condition;
import wxdgaming.spring.boot.excel.store.DataRepository;
import wxdgaming.game.bean.goods.ItemCfg;
import wxdgaming.game.bean.goods.BagChangeDTO4ItemCfg;
import wxdgaming.game.cfg.QTaskTable;
import wxdgaming.game.cfg.bean.QTask;
import wxdgaming.game.core.Reason;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.message.task.*;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.task.TaskInfo;
import wxdgaming.game.server.bean.task.TaskPack;
import wxdgaming.game.server.event.OnLoginBefore;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.game.server.script.inner.InnerService;
import wxdgaming.game.server.script.tips.TipsService;

import java.util.*;

/**
 * 任务基类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-03 15:15
 */
@Slf4j
public abstract class ITaskScript extends HoldRunApplication {

    @Autowired protected BagService bagService;
    @Autowired protected TaskService taskService;
    @Autowired protected TipsService tipsService;
    @Autowired protected InnerService innerService;

    public abstract TaskType type();

    /** 登录的时候检查任务 */
    @OnLoginBefore
    public void onLoginBefore(Player player) {
        TaskPack taskPack = player.getTaskPack();
        initTask(player, taskPack);
    }

    /** 初始化 */
    public void initTask(Player player, TaskPack taskPack) {
        Map<Integer, TaskInfo> integerTaskInfoMap = taskPack.getTasks().row(type());
        if (integerTaskInfoMap.isEmpty()) {
            QTaskTable qTaskTable = DataRepository.getIns().dataTable(QTaskTable.class);
            TreeMap<Integer, QTask> integerQTaskTreeMap = qTaskTable.getTaskGroupMap().get(type());
            QTask qTask = integerQTaskTreeMap.firstEntry().getValue();
            initTaskInfo(player, taskPack, qTask, false);
        }
    }

    protected TaskInfo initTaskInfo(Player player, TaskPack taskPack, QTask qTask, boolean noticeClient) {
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setCfgId(qTask.getId());
        taskService.initTask(player, taskInfo, qTask.getConditionList()/*TODO读取配置表*/);
        taskPack.getTasks().put(type(), taskInfo.getCfgId(), taskInfo);
        log.info("{} 初始化任务 {}, {}, {}", player, type(), qTask.getInnerTaskDetail(), taskInfo);
        /* TODO发送变更列表 */
        if (noticeClient) {
            ResUpdateTaskList resUpdateTaskList = new ResUpdateTaskList();
            TaskBean taskBean = taskInfo.buildTaskBean();
            resUpdateTaskList.getTasks().add(taskBean);
            player.write(resUpdateTaskList);
        }
        return taskInfo;
    }

    /** 更新 */
    public void update(Player player, TaskPack taskPack, List<TaskInfo> changes, Condition condition) {
        Collection<TaskInfo> taskInfos = taskPack.getTasks().row(type()).values();
        for (TaskInfo taskInfo : taskInfos) {
            if (taskInfo.isComplete()) {
                continue;
            }
            boolean update = taskInfo.update(condition);
            if (update) {
                changes.add(taskInfo);
                log.info("{} 更新任务 {}, {}, {}", player, type(), taskInfo.qTask().getInnerTaskDetail(), taskInfo);
            }
        }
    }

    /** 接受任务 */
    public void acceptTask(Player player, TaskPack taskPack, int taskId) {
        QTaskTable qTaskTable = DataRepository.getIns().dataTable(QTaskTable.class);
        TreeMap<Integer, QTask> integerQTaskTreeMap = qTaskTable.getTaskGroupMap().get(type());
        QTask qTask = integerQTaskTreeMap.get(taskId);
        TaskInfo taskInfo = taskPack.getTasks().get(type(), taskId);
        if (taskInfo == null) {
            taskInfo = initTaskInfo(player, taskPack, qTask, false);
        }
        if (taskInfo.getAcceptTime() > 0) {
            log.debug("{} 任务 {} 已经接取", player, qTask.getInnerTaskDetail());
            tipsService.tips(player, "已经接取");
            return;
        }
        ReasonArgs reasonArgs = ReasonArgs.of(Reason.TASK_ACCEPT, "taskCfg=" + taskId);
        if (!ListOf.isEmpty(qTask.getAcceptCost())) {
            BagChangeDTO4ItemCfg changeArgs4ItemCfg = BagChangeDTO4ItemCfg.builder()
                    .setItemCfgList(qTask.getAcceptCost())
                    .setReasonArgs(reasonArgs)
                    .build();
            if (!bagService.checkCost(player, changeArgs4ItemCfg)) {
                return;
            }
            bagService.cost(player, changeArgs4ItemCfg);
        }
        taskInfo.setAcceptTime(System.currentTimeMillis());
        log.info("{} 接取任务：{}, {}, {}", player, type(), qTask.getInnerTaskDetail(), taskInfo.toJSONString());
        ResAcceptTask resAcceptTask = new ResAcceptTask();
        resAcceptTask.setTaskType(type());
        resAcceptTask.setTaskId(taskId);
        resAcceptTask.setTask(taskInfo.buildTaskBean());
        player.write(resAcceptTask);
    }

    /** 提交任务 */
    public void submitTask(Player player, TaskPack taskPack, int taskId) {
        Map<Integer, TaskInfo> taskInfoMap = taskPack.getTasks().row(type());
        TaskInfo taskInfo = taskInfoMap.get(taskId);

        if (!taskInfo.isComplete()) {
            log.debug("{} 任务 {} 没有完成", player, taskId);
            tipsService.tips(player, "任务尚未完成");
            return;
        }

        if (taskInfo.isRewards()) {
            log.debug("{} 任务 {} 已经领取奖励", player, taskId);
            tipsService.tips(player, "已经领取奖励");
            return;
        }

        QTask qTask = taskInfo.qTask();
        ReasonArgs reasonArgs = ReasonArgs.of(Reason.TASK_SUBMIT, "taskCfg=" + taskId);

        if (!ListOf.isEmpty(qTask.getSubmitCost())) {
            BagChangeDTO4ItemCfg changeArgs4ItemCfg = BagChangeDTO4ItemCfg.builder()
                    .setItemCfgList(qTask.getSubmitCost())
                    .setReasonArgs(reasonArgs)
                    .build();
            if (!bagService.checkCost(player, changeArgs4ItemCfg)) {
                return;
            }
            bagService.cost(player, changeArgs4ItemCfg);
        }

        List<ItemCfg> rewards = qTask.getRewards();

        BagChangeDTO4ItemCfg rewardArgs4ItemCfg = BagChangeDTO4ItemCfg.builder()
                .setItemCfgList(rewards)
                .setBagErrorNoticeClient(true)
                .setBagFullSendMail(false)
                .setReasonArgs(reasonArgs)
                .build();

        if (!bagService.gainItemCfg(player, rewardArgs4ItemCfg)) {
            return;
        }

        taskInfo.setRewards(true);
        taskPack.addFinishTask(type(), taskId);
        log.info("{} 提交任务 {}, {}, {}, rewards={}", player, type(), qTask.getInnerTaskDetail(), reasonArgs, rewards);

        ResSubmitTask resSubmitTask = new ResSubmitTask();
        resSubmitTask.setTaskType(type());
        resSubmitTask.setTaskId(taskId);
        QTask nextQTask = qTask.getQTaskAfter();
        if (nextQTask != null) {
            resSubmitTask.setRemove(true);
            taskInfoMap.remove(taskId);
        }
        player.write(resSubmitTask);

        if (nextQTask != null) {
            initTaskInfo(player, taskPack, nextQTask, true);
        }
    }

}
