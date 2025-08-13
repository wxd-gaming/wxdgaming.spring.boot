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


/** 更新等级 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("更新等级")
public class ResUpdateLevel extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 49600219;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 当前等级 */
    @Tag(1) private int level;
    /** 原因 */
    @Tag(2) private String reason;


}
