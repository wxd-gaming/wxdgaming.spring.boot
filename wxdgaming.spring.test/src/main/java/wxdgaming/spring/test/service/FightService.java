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
import wxdgaming.spring.test.buff.AbstractBuffEffectExecutor;
import wxdgaming.spring.test.buff.Buff;
import wxdgaming.spring.test.buff.BuffEffect;
import wxdgaming.spring.test.buff.BuffService;
import wxdgaming.spring.test.map.MapObject;
import wxdgaming.spring.test.map.MapObjectService;
import wxdgaming.spring.test.skill.*;

import java.util.Iterator;
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


    private final HexId hexId = new HexId(1);
    private final MapObjectService mapObjectService;
    private final SkillService skillService;
    private final BuffService buffService;

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
            if (mapObjectService.isDead(attack)) {
                continue;
            }
            {
                executeSkill(attack);
            }
            {
                executeBuff(attack);
            }
        }
    }

    public void executeBuff(MapObject self) {
        List<Buff> buffs = self.getBuffs();
        if (buffs.isEmpty()) {
            return;
        }
        Iterator<Buff> iterator = buffs.iterator();
        while (iterator.hasNext()) {
            Buff buff = iterator.next();
            if (buff.checkOver()) {
                log.debug("{} buff {} 已结束", self, buff);
                iterator.remove();
                continue;
            }
            for (BuffEffect buffEffect : buff.getEffectList()) {
                long diff = MyClock.millis() - buffEffect.getStartTime();
                if (buffEffect.getExecutorDiffTime() > diff) continue;
                buffEffect.setStartTime(MyClock.millis());
                AbstractBuffEffectExecutor abstractBuffEffectExecutor = buffService.getExecutorMap().get(buffEffect.getBuffEffectType());
                List<MapObject> targets = mapObjectService.findTargets(self, buffEffect.getTargetGroup(), buffEffect.getTargetCount());
                abstractBuffEffectExecutor.execute(self, buff, buffEffect, targets);
            }
        }
    }

    public void executeSkill(MapObject attack) {
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
            onExecuteSkill(attack);
            if (!attack.getUseSkill().hasNext()) {
                log.debug("{} 技能 {} 释放完毕", attack, attack.getUseSkill());
                attack.setUseSkill(null);
            }
        }
    }

    public void onExecuteSkill(MapObject attack) {
        SkillExecutor skillExecutor = attack.getUseSkill();
        while (skillExecutor.hasNext()) {
            SkillEffect effect = skillExecutor.get();
            long diff = MyClock.millis() - skillExecutor.getStartTime();
            if (effect.getExecutorDiffTime() > diff) break;
            AbstractSkillEffectExecutor abstractSkillEffectExecutor = skillService.getExecutorMap().get(effect.getSkillEffectType());
            List<MapObject> targets = mapObjectService.findTargets(attack, effect.getTargetGroup(), effect.getTargetCount());
            abstractSkillEffectExecutor.execute(attack, skillExecutor.getSkill(), effect, targets);
            skillExecutor.moveNext();
        }
    }

}
