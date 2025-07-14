package wxdgaming.spring.boot.starter.batis;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 实体类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-01 19:04
 **/
@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
public class IntegerUidEntity extends BaseEntity<Integer> {

    @Id
    @Column(nullable = false)
    private Integer uid;

}
