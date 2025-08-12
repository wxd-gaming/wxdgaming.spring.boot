package  wxdgaming.game.message.global;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.ann.Comment;
import wxdgaming.spring.boot.net.pojo.PojoBase;


/** null */
@Getter
@Setter
@Accessors(chain = true)
@Comment("null")
public class MapBean extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 43427292;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private String key;
    /**  */
    @Tag(2) private String value;


}
