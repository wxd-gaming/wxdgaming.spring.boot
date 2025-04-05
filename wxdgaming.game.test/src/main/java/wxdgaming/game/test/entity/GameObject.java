package wxdgaming.game.test.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.starter.batis.LongUidEntity;

/**
 * 基类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-05 18:24
 **/
@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
public class GameObject extends LongUidEntity {

    private long createTime;

}
