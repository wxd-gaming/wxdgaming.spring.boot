package  wxdgaming.game.message.bag;

import io.protostuff.Tag;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.ann.Comment;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.boot.net.pojo.PojoBase;


/** 导出包名 */
@Getter
@Comment("导出包名")
public enum BagType {

    /**  */
    @Tag(0)
    Bag(0, ""),
    /**  */
    @Tag(1)
    Store(1, ""),

    ;

    private static final Map<Integer, BagType> static_map = MapOf.ofMap(BagType::getCode, BagType.values());

    public static BagType valueOf(int code) {
        return static_map.get(code);
    }

    /** code */
    private final int code;
    /** 备注 */
    private final String command;

    BagType(int code, String command) {
        this.code = code;
        this.command = command;
    }
}
