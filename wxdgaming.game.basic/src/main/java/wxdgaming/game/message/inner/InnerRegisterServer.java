package  wxdgaming.game.message.inner;

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


/** 注册服务 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("注册服务")
public class InnerRegisterServer extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 57048300;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 游戏id */
    @Tag(1) private int gameId;
    /** 主服务器id */
    @Tag(2) private int mainSid;
    /** 服务器id,因为合服可能会导致多个服务器id */
    @Tag(3) private List<Integer> serverIds = new ArrayList<>();
    /** 监听的消息id列表 */
    @Tag(4) private List<Integer> messageIds = new ArrayList<>();
    /** 服务类型 */
    @Tag(5) private ServiceType serviceType;


}
