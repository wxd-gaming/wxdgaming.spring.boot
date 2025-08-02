package wxdgaming.spring.test.skill;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.test.TargetGroup;
import wxdgaming.spring.test.map.MapObject;

import java.util.List;

/**
 * 技能效果
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-31 10:46
 **/
@Getter
@Setter
@Accessors(chain = true)
public abstract class AbstractSkillEffect {

    private final SkillCfg skillCfg;
    /** 效果名称 */
    protected final String name;
    /** 效果作用目标 */
    protected final TargetGroup targetGroup;
    protected final int targetCount;
    /** 执行间隔时间差 */
    protected long executorDiffTime;

    public AbstractSkillEffect(SkillCfg skillCfg, String name, TargetGroup targetGroup, int targetCount) {
        this.skillCfg = skillCfg;
        this.name = name;
        this.targetGroup = targetGroup;
        this.targetCount = targetCount;
    }


    public final void execute(MapObject self, List<MapObject> targets) {
        for (MapObject target : targets) {
            onExecute(self, target);
        }
    }

    protected abstract void onExecute(MapObject self, MapObject target);

}
