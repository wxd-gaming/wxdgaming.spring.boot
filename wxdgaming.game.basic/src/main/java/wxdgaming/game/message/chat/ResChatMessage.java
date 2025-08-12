package  wxdgaming.game.message.chat;

import io.protostuff.Tag;
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


/** 聊天响应 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("聊天响应")
public class ResChatMessage extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 49186680;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private ChatType type;
    /**  */
    @Tag(2) private String content;
    /**  */
    @Tag(3) private List<String> params = new ArrayList<>();
    /** 私聊就是目标玩家id，公会就是公会id */
    @Tag(4) private long targetId;
    /** 发送者id */
    @Tag(5) private long senderId;
    /** 发送者名字 */
    @Tag(6) private String senderName;


}
