package code.db2;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.data.EntityBase;

/**
 * test1
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-31 09:45
 **/
@Getter
@Setter
@Entity
public class Log2 extends EntityBase<Long> {


    @Override public Log2 setCreatedTime(Long createdTime) {
        super.setCreatedTime(createdTime);
        return this;
    }

    @Override public Log2 setUid(Long uid) {
        super.setUid(uid);
        return this;
    }

}
