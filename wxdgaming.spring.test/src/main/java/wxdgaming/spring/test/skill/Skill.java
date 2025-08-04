package wxdgaming.spring.test.skill;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.test.map.MapObject;

import java.util.List;

/**
 * 抽象技能模板类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-01 13:51
 */
@Slf4j
@Getter
@Builder
public class Skill implements Cloneable {

    protected SkillCfg skillCfg;
    /** 效果消耗 */
    protected List<SkillEffect> effectList;
    /** 效果消耗 */
    protected List<SkillCost> costs;

    private long cd;


    @Override public String toString() {
        return skillCfg.toString();
    }

    public boolean checkCost(MapObject mapObject) {
        for (SkillCost cost : costs) {
            if (!cost.check(mapObject))
                return false;
        }
        return true;
    }

    public void cost(MapObject mapObject) {
        for (SkillCost cost : costs) {
            cost.execute(mapObject, skillCfg.toString());
        }
    }

    public void refreshCd() {
        cd = MyClock.millis() + skillCfg.getCd();
    }

    public void resetCd() {
        cd = 0;
    }

    public boolean checkCD() {
        return MyClock.millis() > getCd();
    }


}
