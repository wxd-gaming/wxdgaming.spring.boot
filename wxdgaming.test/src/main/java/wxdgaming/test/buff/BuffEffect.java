package wxdgaming.test.buff;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.test.TargetGroup;

/**
 * buff效果
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-02 20:59
 **/
@Slf4j
@Getter
public class BuffEffect {

    protected final BuffCfg buffCfg;
    /** 效果名称 */
    protected final String name;
    protected final BuffEffectType buffEffectType;
    /** 效果作用目标 */
    protected final TargetGroup targetGroup;
    protected final int targetCount;
    /** 效果开始时间 */
    @Setter protected long startTime;
    /** 执行间隔时间差 */
    protected final long executorDiffTime;


    public BuffEffect(BuffCfg buffCfg, String name, BuffEffectType buffEffectType, TargetGroup targetGroup, int targetCount, long executorDiffTime) {
        this.buffCfg = buffCfg;
        this.name = name;
        this.buffEffectType = buffEffectType;
        this.targetGroup = targetGroup;
        this.targetCount = targetCount;
        this.executorDiffTime = executorDiffTime;
    }

}
