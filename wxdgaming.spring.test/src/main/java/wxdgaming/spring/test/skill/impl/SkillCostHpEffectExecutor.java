package wxdgaming.spring.test.skill.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.util.RandomUtils;
import wxdgaming.spring.test.buff.Buff;
import wxdgaming.spring.test.buff.BuffService;
import wxdgaming.spring.test.map.MapObject;
import wxdgaming.spring.test.skill.AbstractSkillEffectExecutor;
import wxdgaming.spring.test.skill.Skill;
import wxdgaming.spring.test.skill.SkillEffect;
import wxdgaming.spring.test.skill.SkillEffectType;

/**
 * 技能回血效果
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-01 14:03
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

