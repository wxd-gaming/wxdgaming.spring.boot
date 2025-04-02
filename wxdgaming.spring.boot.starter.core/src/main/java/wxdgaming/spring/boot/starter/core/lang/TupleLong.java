package wxdgaming.spring.boot.starter.core.lang;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2020-11-18 13:46
 **/
@Getter
@Setter
@Accessors(chain = true)
public class TupleLong extends ObjectBase implements Serializable {

    protected long left;
    protected long right;

    public TupleLong() {
    }

    public TupleLong(long left, long right) {
        this.left = left;
        this.right = right;
    }
}
