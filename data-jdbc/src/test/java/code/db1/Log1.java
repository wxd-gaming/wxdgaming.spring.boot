package code.db1;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.data.EntityBase;

/**
 * test1
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-31 09:45
 **/
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(indexes = {@Index(columnList = "name")})
public class Log1 extends EntityBase<Long> {

    @Column(columnDefinition = "varchar(255)")
    private String name;
    @Column(columnDefinition = "varchar(255)")
    private String name2;

    @Override public Log1 setCreatedTime(Long createdTime) {
        super.setCreatedTime(createdTime);
        return this;
    }

    @Override public Log1 setUid(Long uid) {
        super.setUid(uid);
        return this;
    }
}
