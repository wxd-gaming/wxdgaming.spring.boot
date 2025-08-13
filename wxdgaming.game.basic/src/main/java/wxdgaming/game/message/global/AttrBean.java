package  wxdgaming.game.message.global;

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


/** 属性 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("属性")
public class AttrBean extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 44783806;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 属性id */
    @Tag(1) private int attrId;
    /** 属性值 */
    @Tag(2) private long value;


}
