package wxdgaming.spring.boot.data.batis;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.lang.ObjectBase;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-16 23:00
 **/
@Getter
@Setter
@Accessors(chain = true)
@Table
@Entity
public class EntityBase<ID> extends ObjectBase {

    @Id
    @Column
    private ID uid;

}
