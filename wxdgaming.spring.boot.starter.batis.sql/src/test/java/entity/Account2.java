package entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.starter.batis.BaseEntity;

/**
 * 复合主键
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-02 13:18
 **/
@Getter
@Setter
@Entity
public class Account2 extends BaseEntity<AccountKey> {

    @EmbeddedId
    AccountKey uid;

}
