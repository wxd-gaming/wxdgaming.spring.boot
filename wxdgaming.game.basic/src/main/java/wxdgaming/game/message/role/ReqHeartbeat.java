package  wxdgaming.game.message.role;

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
import wxdgaming.game.message.global.*;


/** 心跳包 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("心跳包")
public class ReqHeartbeat extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 47320192;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }




}
