package wxdgaming.game.test.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 场景的精灵对象
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-05 19:13
 **/
@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
public class MapSprite extends MapObject {

    private long hp;
    private long map;

}
