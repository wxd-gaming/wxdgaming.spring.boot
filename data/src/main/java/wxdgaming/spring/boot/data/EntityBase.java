package wxdgaming.spring.boot.data;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.lang.ObjectBase;
import wxdgaming.spring.boot.core.timer.MyClock;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-16 23:00
 **/
@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
public class EntityBase<ID> extends ObjectBase implements EntityUID<ID> {

    @Id
    @Column
    private ID uid;
    @Column(nullable = false)
    private Long createdTime = MyClock.millis();

}
