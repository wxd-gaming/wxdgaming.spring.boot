package wxdgaming.spring.test.buff;

import lombok.Builder;
import lombok.Getter;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.test.map.MapObject;
import wxdgaming.spring.test.map.MapObjectService;

import java.util.List;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-02 20:56
 **/
@Getter
@Builder
public class Buff {

    private long uid;
    private BuffCfg buffCfg;
    /** 创建时间 */
    private long createdTime;
    /** 效果消耗 */
    private List<BuffEffect> effectList;
    private MapObject spellcaster;

    public boolean checkOver() {
        if (buffCfg.getDuration() < 1) {
            return false;
        }
        return MyClock.millis() - createdTime > buffCfg.getDuration();
    }

    public void execute(MapObjectService mapObjectService, MapObject self) {

        for (BuffEffect effect : effectList) {

            List<MapObject> targets = mapObjectService.findTargets(self, effect.getTargetGroup(), effect.getTargetCount());
            effect.execute(self, targets);
        }

    }

}
