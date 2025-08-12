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


/** null */
@Getter
@Setter
@Accessors(chain = true)
@Comment("null")
public class ReqLogout extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 44220913;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }




}
