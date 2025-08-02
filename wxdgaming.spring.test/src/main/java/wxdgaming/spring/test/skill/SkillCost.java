package wxdgaming.spring.test.skill;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.test.map.MapObject;

/**
 * 技能消耗
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-31 10:48
 **/
@Slf4j
@Getter
@Setter
@Accessors(chain = true)
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

    public boolean check(MapObject mapObject) {
        return switch (getType()) {
            case MP -> mapObject.getMp() >= getValue();
            case HP -> mapObject.getHp() >= getValue();
            default -> throw new IllegalArgumentException("未知的技能效果类型");
        };
    }

    public void execute(MapObject mapObject, String cmd) {
        switch (getType()) {
            case MP -> {
                mapObject.setMp(mapObject.getMp() - getValue());
                log.debug("{} 技能{}消耗{} - {}, 当前:{}", mapObject, cmd, getType(), getValue(), mapObject.getMp());
            }
            case HP -> {
                mapObject.setHp(mapObject.getHp() - getValue());
                log.debug("{} 技能{}消耗{} - {}, 当前:{}", mapObject, cmd, getType(), getValue(), mapObject.getHp());
            }
            default -> throw new IllegalArgumentException("未知的技能效果类型");
        }
    }

}
