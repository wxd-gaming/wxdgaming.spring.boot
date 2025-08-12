package wxdgaming.game.message.tips;

import io.protostuff.Tag;
import lombok.Getter;
import wxdgaming.spring.boot.core.ann.Comment;
import wxdgaming.spring.boot.core.collection.MapOf;

import java.util.Map;


/** 导出包名 */
@Getter
@Comment("导出包名")
public enum TipsType {

    /** 普通提示 */
    @Tag(0)
    TIP_TYPE_NONE(0, " 普通提示"),
    /** 错误提示 */
    @Tag(1)
    TIP_TYPE_ERROR(1, " 错误提示"),
    /** 成功提示 */
    @Tag(2)
    TIP_TYPE_SUCCESS(2, " 成功提示"),
    /** 警告提示 */
    @Tag(3)
    TIP_TYPE_WARNING(3, " 警告提示"),

    ;

    private static final Map<Integer, TipsType> static_map = MapOf.ofMap(TipsType::getCode, TipsType.values());

    public static TipsType valueOf(int code) {
        return static_map.get(code);
    }

    /** code */
    private final int code;
    /** 备注 */
    private final String command;

    TipsType(int code, String command) {
        this.code = code;
        this.command = command;
    }
}
