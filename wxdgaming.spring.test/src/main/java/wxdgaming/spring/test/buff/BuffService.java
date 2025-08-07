package wxdgaming.spring.test.buff;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.ApplicationContextProvider;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.ann.Init;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.test.TargetGroup;
import wxdgaming.spring.test.map.MapObject;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

/**
 * buff 管理器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-02 20:55
 **/
@Slf4j
@Getter
@Service
public class BuffService implements InitPrint {

    private final HexId hexId = new HexId(1);

    private HashMap<BuffEffectType, AbstractBuffEffectExecutor> executorMap = new HashMap<>();

    @Init
    public void initializeBuffTemplates(ApplicationContextProvider runApplication) {
        HashMap<BuffEffectType, AbstractBuffEffectExecutor> tmpExecutorMap = new HashMap<>();
        Stream<AbstractBuffEffectExecutor> abstractBuffEffectExecutorStream = runApplication.classWithSuper(AbstractBuffEffectExecutor.class);
        abstractBuffEffectExecutorStream.forEach(abstractBuffEffectExecutor -> {
                    AbstractBuffEffectExecutor effectExecutor = tmpExecutorMap.put(abstractBuffEffectExecutor.buffEffectType(), abstractBuffEffectExecutor);
                    if (effectExecutor != null) {
                        throw new RuntimeException("重复注册 buff 效果执行器");
                    }
                }
        );
        executorMap = tmpExecutorMap;
    }

    public Buff createBuff(MapObject spellcaster) {
        BuffCfg buffCfg = BuffCfg.builder()
                .id(1)
                .name("灼烧")
                .clientShow(true)
                .duration(5000)
                .build();
        BuffEffect buffEffect = new BuffEffect(buffCfg, buffCfg.getName(), BuffEffectType.CostHp, TargetGroup.Enemy, 1, 1000);
        return new Buff(hexId.newId(), buffCfg, MyClock.millis(), List.of(buffEffect), spellcaster);
    }

    public boolean addBuff(MapObject self, Buff buff) {
        List<Buff> buffs = self.getBuffs();
        buffs.removeIf(b -> {
            if (b.getBuffCfg().getId() == buff.getBuffCfg().getId()) {
                log.debug("{} buff {} 覆盖 {}", self, buff, b);
                return true;
            }
            return false;
        });
        buffs.add(buff);
        return true;
    }

}
