package  wxdgaming.game.message.task;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.ann.Comment;
import wxdgaming.spring.boot.net.pojo.PojoBase;


/** 接受任务 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("接受任务")
public class ResAcceptTask extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 48289835;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private TaskType taskType;
    /**  */
    @Tag(2) private int taskId;
    /**  */
    @Tag(3) private TaskBean task;


}
