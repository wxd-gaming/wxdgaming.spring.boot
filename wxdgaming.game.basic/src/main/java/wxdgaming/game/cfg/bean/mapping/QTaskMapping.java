package wxdgaming.game.cfg.bean.mapping;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.core.lang.ObjectBase;
import wxdgaming.spring.boot.excel.store.DataKey;
import wxdgaming.spring.boot.excel.store.DataMapping;

import java.io.Serializable;
import java.util.*;

/**
 * excel 构建 任务集合, src/main/cfg/任务成就.xlsx, q_task,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DataMapping(name = "q_task", comment = "任务集合", excelPath = "src/main/cfg/任务成就.xlsx", sheetName = "q_task")
public abstract class QTaskMapping extends ObjectBase implements Serializable, DataKey {

    /** 主键id */
    protected int id;
    /** 任务类型 */
    protected wxdgaming.game.message.task.TaskType taskType;
    /** 上一个任务ID */
    protected int before;
    /** 下一个任务ID */
    protected int after;
    /** 任务名称 */
    protected String name;
    /** 任务说明 */
    protected String description;
    /** 等级 */
    protected int min_lv;
    /** 等级 */
    protected int max_lv;
    /** 任务条件 */
    protected final List<wxdgaming.spring.boot.core.lang.condition.Condition> conditionList = new ArrayList<>();
    /** 任务接取的时候需要扣除的道具 */
    protected final List<wxdgaming.game.bean.goods.ItemCfg> acceptCost = new ArrayList<>();
    /** 任务奖励 */
    protected final List<wxdgaming.game.bean.goods.ItemCfg> rewards = new ArrayList<>();
    /** 任务提交的时候需要扣除的道具 */
    protected final List<wxdgaming.game.bean.goods.ItemCfg> submitCost = new ArrayList<>();

    public Object key() {
        return id;
    }

}
