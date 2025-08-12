package  wxdgaming.game.message.task;

import io.protostuff.Tag;
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


/** 任务列表 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("任务列表")
public class ReqTaskList extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 46251239;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private TaskType taskType;


}
