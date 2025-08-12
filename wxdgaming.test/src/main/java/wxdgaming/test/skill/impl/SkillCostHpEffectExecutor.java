package wxdgaming.test.skill.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.util.RandomUtils;
import wxdgaming.test.buff.Buff;
import wxdgaming.test.buff.BuffService;
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
public class SkillCostHpEffectExecutor extends AbstractSkillEffectExecutor {

    final BuffService buffService;

    public SkillCostHpEffectExecutor(BuffService buffService) {this.buffService = buffService;}

    @Override public SkillEffectType skillEffectType() {
        return SkillEffectType.CostHp;
    }

    @Override protected void onExecute(MapObject self, Skill skill, SkillEffect skillEffect, MapObject target) {
        int random = RandomUtils.random(100, 300);
        getMapObjectService().costHp(target, random);
        log.debug(
                "{}使用了技能{}({})，伤害{} {}点生命值, 当前生命值：{}",
                self, skill.getSkillCfg(), skillEffect.getName(), target.getName(), random, target.getHp()
        );
        boolean randomBoolean = RandomUtils.randomBoolean(2000);
        if (randomBoolean) {
            log.debug("{}使用了技能{}({})，对 {} 触发buff 灼烧 ", self, skill.getSkillCfg(), skillEffect.getName(), target.getName());
            Buff buff = buffService.createBuff(self);
            buffService.addBuff(target, buff);
        }
    }
}

