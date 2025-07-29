package wxdgaming.spring.boot.batis;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.batis.ann.DbColumn;

/**
 * Integer
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 13:02
 **/
@Getter
@Setter
public class EntityIntegerUID extends Entity {

    @DbColumn(key = true)
    @JSONField(ordinal = 1)
    private int uid = 0;

    @Override public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        EntityIntegerUID that = (EntityIntegerUID) o;
        return getUid() == that.getUid();
    }

    @Override public int hashCode() {
        return getUid();
    }
}
