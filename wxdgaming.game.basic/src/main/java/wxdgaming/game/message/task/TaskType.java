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


/** 任务类型 */
@Getter
@Comment("任务类型")
public enum TaskType {

    /**  */
    @Tag(0)
    TASK_TYPE_NONE(0, ""),
    /**  */
    @Tag(1)
    Main(1, ""),

    ;

    private static final Map<Integer, TaskType> static_map = MapOf.ofMap(TaskType::getCode, TaskType.values());

    public static TaskType valueOf(int code) {
        return static_map.get(code);
    }

    /** code */
    private final int code;
    /** 备注 */
    private final String command;

    TaskType(int code, String command) {
        this.code = code;
        this.command = command;
    }
}
