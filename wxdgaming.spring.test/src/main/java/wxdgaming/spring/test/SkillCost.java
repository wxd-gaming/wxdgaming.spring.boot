package wxdgaming.spring.test;

import lombok.Getter;
import lombok.Setter;

/**
 * 技能消耗
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-31 10:48
 **/
@Getter
@Setter
public class SkillCost {

    public enum SkillCostType {
        HP,
        MP,
    }

    private SkillCostType type;
    private int value;

    public SkillCost(SkillCostType type, int value) {
        this.type = type;
        this.value = value;
    }
}
