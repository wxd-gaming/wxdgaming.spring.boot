package wxdgaming.spring.test.skill.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.util.RandomUtils;
import wxdgaming.spring.test.TargetGroup;
import wxdgaming.spring.test.map.MapObject;
import wxdgaming.spring.test.skill.AbstractSkillEffect;
import wxdgaming.spring.test.skill.SkillCfg;

/**
 * 技能回血效果
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-01 14:03
 **/
@Slf4j
@Getter
public class SkillCostHpEffect extends AbstractSkillEffect {

    public SkillCostHpEffect(SkillCfg skillCfg, TargetGroup targetType, int targetCount) {
        super(skillCfg, "扣血", targetType, targetCount);
    }

    @Override protected void onExecute(MapObject self, MapObject target) {
        int random = RandomUtils.random(10, 300);
        int oldHealth = target.getHp();
        target.setHp(oldHealth - random);
        if (target.getHp() < 0) {
            target.setHp(0);
        }
        log.debug(
                "{}使用了技能{}({})，伤害{} {}点生命值, 当前生命值：{}",
                self, getSkillCfg(), getName(), target.getName(), random, target.getHp()
        );
    }
}
