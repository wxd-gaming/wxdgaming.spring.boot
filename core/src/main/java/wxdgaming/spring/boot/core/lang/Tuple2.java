package wxdgaming.spring.boot.core.lang;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.json.FastJsonUtil;

import java.io.Serializable;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2020-11-18 13:46
 **/
@Getter
@Setter
@Accessors(chain = true)
public class Tuple2<L, R> extends ObjectBase implements Serializable {

    private static final long serialVersionUID = 1L;

    public void t0(String[] args) {
        Tuple2<Integer, Integer> tuple2 = new Tuple2<>(1, 1);
        String s = FastJsonUtil.toJsonFmt(tuple2);
        System.out.println(s);
        Tuple2<Integer, Integer> object = FastJsonUtil.parse(s, Tuple2.class);
        System.out.println(object);
    }

    protected L left;
    protected R right;

    public Tuple2() {
    }

    public Tuple2(L left, R right) {
        this.left = left;
        this.right = right;
    }
}
