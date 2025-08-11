package  wxdgaming.spring.boot.net.module.inner.message;

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


/** 执行同步等待消息 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("执行同步等待消息")
public class ResRemote extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 65602206;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private long uid;
    /** 用于验证的消息 */
    @Tag(2) private String token;
    /** 1表示压缩过 */
    @Tag(4) private int gzip;
    /** 用JsonObject来解析 */
    @Tag(5) private String params;


}
