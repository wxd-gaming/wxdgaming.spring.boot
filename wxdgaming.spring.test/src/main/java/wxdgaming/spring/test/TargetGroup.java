package wxdgaming.spring.test;

import lombok.Getter;
import wxdgaming.spring.boot.core.collection.MapOf;

import java.util.Map;

/**
 * 目标
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-01 15:31
 **/
@Getter
public enum TargetGroup {
    Self(1, "自己"),
    Enemy(2, "敌方"),
    Friend(3, "友方"),
    All(4, "全体"),
    Team(5, "队友"),
    Target(6, "目标"),
    ;

    private static final Map<Integer, TargetGroup> static_map = MapOf.ofMap(TargetGroup::getCode, TargetGroup.values());

    public static TargetGroup of(int value) {
        return static_map.get(value);
    }

    public static TargetGroup ofOrException(int value) {
        TargetGroup tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;

    TargetGroup(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

}