package wxdgaming.spring.test.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.util.RandomUtils;
import wxdgaming.spring.test.*;
import wxdgaming.spring.test.Character;

import java.util.List;

/**
 * 技能回血效果
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-01 14:03
 **/
@Slf4j
@Getter
public class SkillCostHpEffect extends AbstractSkillEffect {

    public SkillCostHpEffect(SkillCfg skillCfg, TargetType targetType, List<SkillCost> costs) {
        super(skillCfg, "扣血", targetType, costs);
    }

    @Override protected void onExecute(Character caster, Character target) {
        int random = RandomUtils.random(10, 30);
        int oldHealth = target.getHp();
        target.setHp(oldHealth - random);
        if (target.getHp() < 0) {
            target.setHp(0);
        }
        log.debug("{}使用了技能{}({})，伤害{} {}点生命值, 当前生命：{}", caster.getName(), getSkillCfg(), getName(), target.getName(), random, target.getHp());
    }
}
