package  wxdgaming.game.message.task;

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


/** null */
@Getter
@Setter
@Accessors(chain = true)
@Comment("null")
public class TaskBean extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 42597836;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /** 任务id */
    @Tag(2) private int taskId;
    /** 是否接受 */
    @Tag(3) private boolean accept;
    /** 是否完成 */
    @Tag(4) private boolean completed;
    /** 是否领取奖励 */
    @Tag(5) private boolean reward;
    /** 进度,当任务completed = true,此参数空 */
    @Tag(6) private List<Long> progresses = new ArrayList<>();


}
