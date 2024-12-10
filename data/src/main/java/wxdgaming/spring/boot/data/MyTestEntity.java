package wxdgaming.spring.boot.data;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * 测试用的实体类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-05 14:19
 **/
@Getter
@Setter
@Entity
@Table(indexes = {@Index(columnList = "name")})
public class MyTestEntity extends EntityBase<Long> {

    private String name;

    public MyTestEntity() {
        setUid(System.nanoTime());
    }

}
