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
public class SkillHealMpEffect extends AbstractSkillEffect {


    public SkillHealMpEffect(SkillCfg skillCfg, TargetGroup targetGroup, int targetCount) {
        super(skillCfg, "回蓝", targetGroup,targetCount );
    }

    @Override protected void onExecute(MapObject self, MapObject target) {
        int random = RandomUtils.random(10, 30);
        int oldMap = target.getMp();
        target.setMp(oldMap + random);
        log.debug(
                "{}使用了技能{}({})，恢复了{} {}点魔法值, 当前魔法值：{}",
                self.getName(), getSkillCfg(), getName(), target.getName(), random, target.getMp()
        );
    }

}
