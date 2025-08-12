package wxdgaming.test.map;

import lombok.Getter;
import wxdgaming.spring.boot.core.collection.MapOf;

import java.util.Map;

/**
 * 场景对象
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-02 21:45
 **/
@Getter
public enum MapObjectType {

    Player(1, "玩家"),
    Monster(2, "怪物"),

    ;

    private static final Map<Integer, MapObjectType> static_map = MapOf.ofMap(MapObjectType::getCode, MapObjectType.values());

    public static MapObjectType of(int value) {
        return static_map.get(value);
    }

    public static MapObjectType ofOrException(int value) {
        MapObjectType tmp = static_map.get(value);
        if (tmp == null) throw new RuntimeException("查找失败 " + value);
        return tmp;
    }

    private final int code;
    private final String comment;

    MapObjectType(int code, String comment) {
        this.code = code;
        this.comment = comment;
    }

}