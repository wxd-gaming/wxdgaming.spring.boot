package wxdgaming.spring.boot.starter.batis;

import jakarta.persistence.MappedSuperclass;
import wxdgaming.spring.boot.starter.core.lang.ObjectBase;

/**
 * 实体类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-01 19:04
 **/
@MappedSuperclass
public abstract class BaseEntity<ID> extends ObjectBase {

    public abstract ID getUid();

}
