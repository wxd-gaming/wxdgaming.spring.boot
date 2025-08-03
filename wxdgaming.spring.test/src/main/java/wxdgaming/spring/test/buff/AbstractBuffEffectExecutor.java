package wxdgaming.spring.test.buff;

import wxdgaming.spring.test.map.MapObject;

import java.util.List;

/**
 * buff执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-03 09:52
 **/
public abstract class AbstractBuffEffectExecutor {

    public abstract BuffEffectType buffEffectType();

    public final void execute(MapObject self, Buff buff, BuffEffect buffEffect, List<MapObject> targets) {
        for (MapObject target : targets) {
            onExecute(self, buff, buffEffect, target);
        }
    }

    protected abstract void onExecute(MapObject self, Buff buff, BuffEffect buffEffect, MapObject target);

}
