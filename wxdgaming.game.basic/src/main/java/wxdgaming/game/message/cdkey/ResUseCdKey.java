package  wxdgaming.game.message.cdkey;

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


/** 响应使用cdkey */
@Getter
@Setter
@Accessors(chain = true)
@Comment("响应使用cdkey")
public class ResUseCdKey extends PojoBase {

    /** 消息ID */
    public static int _msgId() {
        return 46805964;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private String cdKey;


}
