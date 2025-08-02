package wxdgaming.spring.test;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.util.AssertUtil;

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
    protected final TargetType targetType;
    /** 效果消耗 */
    protected final List<SkillCost> costs;
    /** 执行间隔时间差 */
    protected long executorDiffTime;

    public AbstractSkillEffect(SkillCfg skillCfg, String name, TargetType targetType, List<SkillCost> costs) {
        this.skillCfg = skillCfg;
        this.name = name;
        this.targetType = targetType;
        this.costs = costs;
    }

    protected void cost(Character caster) {
        for (SkillCost cost : costs) {
            switch (cost.getType()) {
                case MP:
                    AssertUtil.assertTrue(caster.getMp() >= cost.getValue(), "%s魔法值不足，无法使用%s", caster.getName(), name);
                    caster.setMp(caster.getMp() - cost.getValue());
                    break;
                case HP:
                    AssertUtil.assertTrue(caster.getHp() >= cost.getValue(), "%s生命值不足，无法使用%s", caster.getName(), name);
                    caster.setHp(caster.getHp() - cost.getValue());
                    break;
                default:
                    throw new IllegalArgumentException("未知的技能效果类型");
            }
        }
    }

    public final void execute(Character self, List<Character> targets) {
        cost(self);
        for (Character target : targets) {
            if (targetType == TargetType.Self) {
                if (self != target) continue;
            } else if (targetType == TargetType.ALL_ENEMIES) {
                if (self == target) continue;/*自己不是自己的敌方*/
            } else if (targetType == TargetType.ALL_ALLIES) {
                if (self != target) continue;/*自己和自己是友方*/
            }
            onExecute(self, target);
        }
    }

    protected abstract void onExecute(Character caster, Character target);

}
