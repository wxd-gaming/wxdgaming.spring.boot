package  wxdgaming.game.message.bag;

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
public class ItemBean extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 41077900;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private long uid;
    /**  */
    @Tag(2) private int itemId;
    /**  */
    @Tag(3) private long count;
    /**  */
    @Tag(4) private boolean bind;
    /**  */
    @Tag(5) private long expireTime;


}
