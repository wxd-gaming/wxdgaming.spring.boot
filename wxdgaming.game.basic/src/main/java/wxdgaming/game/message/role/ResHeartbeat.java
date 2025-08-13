package  wxdgaming.game.message.role;

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
import wxdgaming.game.message.global.*;
import wxdgaming.spring.boot.core.ann.Comment;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.boot.net.pojo.PojoBase;


/** 心跳包响应 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("心跳包响应")
public class ResHeartbeat extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 47340624;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 当前服务器utc时间戳 */
    @Tag(1) private long timestamp;


}
