package wxdgaming.spring.test;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 游戏对象
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-02 21:36
 **/
@Getter
@Setter
@Accessors(chain = true)
public class GameObject {

    private long uid;
    private String name;

    @Override public String toString() {
        return "GameObject{name='%s', uid=%d}".formatted(name, uid);
    }
}
