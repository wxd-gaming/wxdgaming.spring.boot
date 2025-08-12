package wxdgaming.test.buff;

import lombok.Getter;
import wxdgaming.spring.boot.core.collection.MapOf;

import java.util.Map;

/**
 * buff类型
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-03 09:56
 **/
@Getter
public enum BuffEffectType {
    None(0, "默认值"),
    HealHp(1, "回复血量"),
    HealMp(2, "回复蓝量"),
    AddHp(3, "增加血量"),
    AddMp(4, "增加蓝量"),
    AddAttack(5, "增加攻击力"),
    AddDefense(6, "增加防御力"),
    AddSpeed(7, "增加速度"),
    CostHp(8, "扣血量"),
    CostMp(9, "扣蓝量"),
    ;

    private static final Map<Integer, BuffEffectType> static_map = MapOf.ofMap(BuffEffectType::getCode, BuffEffectType.values());

    public static BuffEffectType of(int value) {
        return static_map.get(value);
    }

    public static BuffEffectType ofOrException(int value) {
        BuffEffectType tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;

    BuffEffectType(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

}