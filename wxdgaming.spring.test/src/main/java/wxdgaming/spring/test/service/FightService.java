package wxdgaming.spring.test.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.executor.ExecutorEvent;
import wxdgaming.spring.boot.core.executor.ExecutorFactory;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.test.buff.BuffService;
import wxdgaming.spring.test.map.MapObject;
import wxdgaming.spring.test.map.MapObjectService;
import wxdgaming.spring.test.skill.AbstractSkillEffect;
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
            {
                skillService.execute(attack);
            }
            {
                buffService.execute(attack);
            }
        }
    }

}
