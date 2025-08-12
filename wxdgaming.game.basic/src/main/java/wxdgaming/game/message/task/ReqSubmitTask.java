package  wxdgaming.game.message.task;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.ann.Comment;
import wxdgaming.spring.boot.net.pojo.PojoBase;


/** 提交任务 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("提交任务")
public class ReqSubmitTask extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 48636847;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private TaskType taskType;
    /**  */
    @Tag(2) private int taskId;


}
