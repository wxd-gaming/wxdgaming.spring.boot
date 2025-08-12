package wxdgaming.test.skill;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.test.TargetGroup;

/**
 * 技能效果
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-31 10:46
 **/
@Getter
@Setter
@Accessors(chain = true)
public class SkillEffect {

    private final SkillCfg skillCfg;
    /** 效果名称 */
    protected final String name;
    /** 技能效果类型 */
    protected final SkillEffectType skillEffectType;
    /** 效果作用目标 */
    protected final TargetGroup targetGroup;
    protected final int targetCount;
    /** 执行间隔时间差 */
    protected long executorDiffTime;

    public SkillEffect(SkillCfg skillCfg, String name, SkillEffectType skillEffectType, TargetGroup targetGroup, int targetCount) {
        this.skillCfg = skillCfg;
        this.name = name;
        this.skillEffectType = skillEffectType;
        this.targetGroup = targetGroup;
        this.targetCount = targetCount;
    }

}
