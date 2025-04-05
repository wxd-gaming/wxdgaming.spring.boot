package wxdgaming.game.test.entity.db;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.test.entity.MapSprite;

/**
 * 角色
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-05 18:23
 **/
@Getter
@Setter
@Entity
public class Role extends MapSprite {


    @Column(length = 64)
    private String account;

}
