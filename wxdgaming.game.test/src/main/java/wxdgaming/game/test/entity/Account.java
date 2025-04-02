package wxdgaming.game.test.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.starter.batis.LongUidEntity;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-02 13:18
 **/
@Getter
@Setter
@Entity
@Table(indexes = {@Index(columnList = "account", unique = true)})
public class Account extends LongUidEntity {

    @Column
    private String account;

}
