package wxdgaming.spring.test;

import lombok.Getter;
import wxdgaming.spring.boot.core.collection.MapOf;

import java.util.Map;

/**
 * 目标
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-01 15:31
 **/
@Getter
public enum TargetType {
    None(0, "默认值"),
    Self(1, "自身"),
    All(2, "全体"),
    ALL_ENEMIES(4, "敌方全体"),
    ALL_ALLIES(7, "友方全体"),
    ;

    private static final Map<Integer, TargetType> static_map = MapOf.ofMap(TargetType::getCode, TargetType.values());

    public static TargetType of(int value) {
        return static_map.get(value);
    }

    public static TargetType ofOrException(int value) {
        TargetType tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;

    TargetType(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

}