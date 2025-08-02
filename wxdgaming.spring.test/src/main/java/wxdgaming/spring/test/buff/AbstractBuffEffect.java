package wxdgaming.spring.test.buff;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.test.TargetGroup;
import wxdgaming.spring.test.map.MapObject;

import java.util.List;

/**
 * buff效果
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-02 20:59
 **/
@Slf4j
@Getter
public abstract class AbstractBuffEffect {

    protected final BuffCfg buffCfg;
    /** 效果名称 */
    protected final String name;
    /** 效果作用目标 */
    protected final TargetGroup targetGroup;
    protected long startTime;
    /** 执行间隔时间差 */
    protected long executorDiffTime;

    public AbstractBuffEffect(BuffCfg buffCfg, String name, TargetGroup targetGroup) {
        this.buffCfg = buffCfg;
        this.name = name;
        this.targetGroup = targetGroup;
    }


    public final void execute(MapObject self, List<MapObject> targets) {
        for (MapObject target : targets) {
            onExecute(self, target);
        }
    }

    protected abstract void onExecute(MapObject self, MapObject target);


}
