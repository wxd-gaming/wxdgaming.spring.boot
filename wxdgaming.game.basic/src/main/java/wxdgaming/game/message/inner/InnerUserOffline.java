package  wxdgaming.game.message.inner;

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


/** 玩家离线 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("玩家离线")
public class InnerUserOffline extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 53197558;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 客户端sessionId */
    @Tag(1) private long clientSessionId;
    /** 账号 */
    @Tag(2) private String account;


}
