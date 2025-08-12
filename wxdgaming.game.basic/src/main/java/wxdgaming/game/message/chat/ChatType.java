package  wxdgaming.game.message.chat;

import io.protostuff.Tag;
import lombok.Getter;
import wxdgaming.spring.boot.core.ann.Comment;
import wxdgaming.spring.boot.core.collection.MapOf;

import java.util.Map;


/** 导出包名 */
@Getter
@Comment("导出包名")
public enum ChatType {

    /** 占位符 */
    @Tag(0)
    Chat_TYPE_NONE(0, "占位符"),
    /** 世界聊天 */
    @Tag(1)
    Chat_TYPE_World(1, "世界聊天"),
    /** 私聊 */
    @Tag(2)
    Chat_TYPE_Private(2, "私聊"),
    /** 公会聊天 */
    @Tag(3)
    Chat_TYPE_Guild(3, "公会聊天"),
    /** 系统消息 */
    @Tag(4)
    Chat_TYPE_System(4, "系统消息"),

    ;

    private static final Map<Integer, ChatType> static_map = MapOf.ofMap(ChatType::getCode, ChatType.values());

    public static ChatType valueOf(int code) {
        return static_map.get(code);
    }

    /** code */
    private final int code;
    /** 备注 */
    private final String command;

    ChatType(int code, String command) {
        this.code = code;
        this.command = command;
    }
}
