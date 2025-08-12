package wxdgaming.test.skill.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.util.RandomUtils;
import wxdgaming.test.map.MapObject;
import wxdgaming.test.skill.AbstractSkillEffectExecutor;
import wxdgaming.test.skill.Skill;
import wxdgaming.test.skill.SkillEffect;
import wxdgaming.test.skill.SkillEffectType;

/**
 * 技能回血效果
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-01 14:03
 **/
@Slf4j
@Getter
@Component
public class SkillHealMpEffectExecutor extends AbstractSkillEffectExecutor {

    @Override public SkillEffectType skillEffectType() {
        return SkillEffectType.HealMp;
    }

    @Override protected void onExecute(MapObject self, Skill skill, SkillEffect skillEffect, MapObject target) {
        int random = RandomUtils.random(10, 30);
        getMapObjectService().healMp(target, random);
        log.debug(
                "{}使用了技能{}({})，恢复了{} {}点魔法值, 当前魔法值：{}",
                self.getName(), skill.getSkillCfg(), skillEffect.getName(), target.getName(), random, target.getMp()
        );
    }

}
