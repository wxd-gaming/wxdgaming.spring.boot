package wxdgaming.spring.boot.data.batis;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.lang.ObjectBase;

/**
 * 自增id
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-16 23:00
 **/
@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
public class EntityAutoBase<ID> extends ObjectBase {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private ID uid;
    @Column
    private long createdTime;

}
