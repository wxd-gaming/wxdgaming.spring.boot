package wxdgaming.spring.test.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.executor.ExecutorEvent;
import wxdgaming.spring.boot.core.executor.ExecutorFactory;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.boot.core.util.RandomUtils;
import wxdgaming.spring.test.buff.BuffService;
import wxdgaming.spring.test.map.MapObject;
import wxdgaming.spring.test.map.MapObjectService;
import wxdgaming.spring.test.skill.AbstractSkillEffect;
import wxdgaming.spring.test.skill.Skill;
import wxdgaming.spring.test.skill.SkillExecutor;
import wxdgaming.spring.test.skill.SkillService;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 战斗服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-02 22:12
 **/
@Slf4j
@Getter
@Service
public class FightService extends ExecutorEvent implements InitPrint {

    private final SkillService skillService;
    private final MapObjectService mapObjectService;
    private final BuffService buffService;
    private final HexId hexId = new HexId(1);

    @Autowired
    public FightService(MapObjectService mapObjectService, SkillService skillService, BuffService buffService) {
        this.mapObjectService = mapObjectService;
        this.skillService = skillService;
        this.buffService = buffService;
    }

    @Start
    public void start() {
        ExecutorFactory.getExecutorServiceLogic().scheduleAtFixedRate(this, 16, 16, TimeUnit.MILLISECONDS);
    }

    @Override public void onEvent() throws Exception {
        for (MapObject attack : mapObjectService.getMapObjectMap().values()) {
            if (attack.getUseSkill() == null) {
                boolean b = RandomUtils.randomBoolean(20);
                if (b) {
                    Skill skill = attack.randomSkill();
                    if (skill != null) {
                        SkillExecutor skillExecutor = SkillExecutor.builder()
                                .uid(hexId.newId())
                                .self(attack)
                                .skill(skill)
                                .build();
                        attack.setUseSkill(skillExecutor);
                        log.debug("{} 释放技能 {}", attack, skill);
                    }
                }
            }
            if (attack.getUseSkill() != null) {
                execute(attack);
                if (!attack.getUseSkill().hasNext()) {
                    log.debug("{} 技能 {} 释放完毕", attack, attack.getUseSkill());
                    attack.setUseSkill(null);
                }
            }
        }
    }

    public void execute(MapObject attack) {
        SkillExecutor useSkill = attack.getUseSkill();
        while (useSkill.hasNext()) {
            AbstractSkillEffect effect = useSkill.get();
            long diff = MyClock.millis() - useSkill.getStartTime();
            if (effect.getExecutorDiffTime() > diff) break;
            List<MapObject> targets = mapObjectService.findTargets(attack, effect.getTargetGroup(), effect.getTargetCount());
            effect.execute(attack, targets);
            useSkill.moveNext();
        }
    }

}
