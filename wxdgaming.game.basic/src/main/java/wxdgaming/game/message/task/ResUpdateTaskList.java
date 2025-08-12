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


/** 更新任务列表 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("更新任务列表")
public class ResUpdateTaskList extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 53081705;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(2) private List<TaskBean> tasks = new ArrayList<>();


}
