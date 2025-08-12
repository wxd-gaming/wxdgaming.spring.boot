package  wxdgaming.game.message.role;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.ann.Comment;
import wxdgaming.spring.boot.net.pojo.PojoBase;


/** 角色信息 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("角色信息")
public class RoleBean extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 42578250;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private long rid;
    /**  */
    @Tag(2) private String name;
    /**  */
    @Tag(3) private int level;
    /**  */
    @Tag(4) private long exp;


}
