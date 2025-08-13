package  wxdgaming.game.message.bag;

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


/** 响应背包信息 */
@Getter
@Setter
@Accessors(chain = true)
@Comment("响应背包信息")
public class ResBagInfo extends PojoBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    /** 消息ID */
    public static int _msgId() {
        return 43143510;
    }

    /** 消息ID */
    public int msgId() {
        return _msgId();
    }


    /**  */
    @Tag(1) private BagType bagType;
    /** 所有的货币 */
    @Tag(2) private Map<Integer, Long> currencyMap = new LinkedHashMap<>();
    /** 所有的物品 */
    @Tag(3) private Map<Integer, ItemBean> items = new LinkedHashMap<>();


}
