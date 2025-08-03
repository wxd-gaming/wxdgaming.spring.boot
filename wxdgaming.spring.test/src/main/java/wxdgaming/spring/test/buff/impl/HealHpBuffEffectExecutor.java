package wxdgaming.spring.test.buff.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.util.RandomUtils;
import wxdgaming.spring.test.buff.AbstractBuffEffectExecutor;
import wxdgaming.spring.test.buff.Buff;
import wxdgaming.spring.test.buff.BuffEffect;
import wxdgaming.spring.test.buff.BuffEffectType;
import wxdgaming.spring.test.map.MapObject;

/**
 * 技能回血效果
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-01 14:03
 **/
@Slf4j
@Getter
public class HealHpBuffEffectExecutor extends AbstractBuffEffectExecutor {

    @Override public BuffEffectType buffEffectType() {
        return BuffEffectType.HealHp;
    }

    @Override protected void onExecute(MapObject self, Buff buff, BuffEffect buffEffect, MapObject target) {
        int random = RandomUtils.random(10, 30);
        int oldHealth = target.getHp();
        target.setHp(oldHealth + random);
        log.debug(
                "{}触发buff{}({})，恢复了{} {}点生命值, 当前生命值：{}",
                self.getName(), buff.getBuffCfg(), buffEffect.getName(), target.getName(), random, target.getHp()
        );
    }

}
