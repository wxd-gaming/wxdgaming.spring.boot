package wxdgaming.spring.test.buff;

import lombok.Builder;
import lombok.Getter;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.test.map.MapObject;

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
    /** 是谁释放的 */
    private MapObject spellcaster;

    public boolean checkOver() {
        if (buffCfg.getDuration() < 1) {
            return false;
        }
        return MyClock.millis() - createdTime > buffCfg.getDuration();
    }

    @Override public String toString() {
        return buffCfg.toString();
    }
}
