package  wxdgaming.game.message.tips;

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


/** 提示内容 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("提示内容")
public class ResTips extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 42002782;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private TipsType type;
    /**  */
    @Tag(2) private String content;
    /**  */
    @Tag(3) private List<String> params = new ArrayList<>();
    /** 提示消息id，如果客户端在监听这个id */
    @Tag(4) private int resMessageId;
    /** 触发原因，可能null */
    @Tag(5) private String reason;


}
