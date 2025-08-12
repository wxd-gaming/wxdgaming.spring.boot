package  wxdgaming.game.message.role;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.ann.Comment;
import wxdgaming.spring.boot.net.pojo.PojoBase;

import java.util.ArrayList;
import java.util.List;


/** 登录响应 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("登录响应")
public class ResLogin extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 42848768;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 用户id */
    @Tag(1) private String userId;
    /** 账号 */
    @Tag(2) private String account;
    /** 当前选择的区服id */
    @Tag(3) private int sid;
    /** 角色列表 */
    @Tag(4) private List<RoleBean> roles = new ArrayList<>();


}
