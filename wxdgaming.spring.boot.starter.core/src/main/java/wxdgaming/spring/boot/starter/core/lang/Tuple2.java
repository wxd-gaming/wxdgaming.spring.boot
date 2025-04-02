package wxdgaming.spring.boot.starter.core.lang;


import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2020-11-18 13:46
 **/
@Getter
@Setter
@Accessors(chain = true)
public class Tuple2<L, R> extends ObjectBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    protected L left;
    protected R right;

    public Tuple2() {
    }

    @JSONCreator
    public Tuple2(@JSONField(name = "left") L left, @JSONField(name = "right") R right) {
        this.left = left;
        this.right = right;
    }

}
