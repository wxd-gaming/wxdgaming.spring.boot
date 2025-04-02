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
public class TupleInt extends ObjectBase implements Serializable {

    protected int left;
    protected int right;

    public TupleInt() {
    }

    public TupleInt(int left, int right) {
        this.left = left;
        this.right = right;
    }
}
