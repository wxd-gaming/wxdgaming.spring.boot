package  wxdgaming.game.message.role;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.game.message.global.MapBean;
import wxdgaming.spring.boot.core.ann.Comment;
import wxdgaming.spring.boot.net.pojo.PojoBase;

import java.util.ArrayList;
import java.util.List;


/** 登录请求 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("登录请求")
public class ReqLogin extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 42828529;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private String account;
    /** 用于验证的令牌 */
    @Tag(2) private String token;
    /** 当前选择的区服id */
    @Tag(3) private int sid;
    /** 客户端特殊列表 */
    @Tag(4) private List<MapBean> clientParams = new ArrayList<>();


}
