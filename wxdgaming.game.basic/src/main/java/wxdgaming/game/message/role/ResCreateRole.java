package  wxdgaming.game.message.role;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.ann.Comment;
import wxdgaming.spring.boot.net.pojo.PojoBase;


/** 创建角色响应 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("创建角色响应")
public class ResCreateRole extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 48312342;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private RoleBean role;


}
