package wxdgaming.game.test.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 场景对象
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-05 18:54
 **/
@Getter
@Setter
@Accessors(chain = true)
@MappedSuperclass
public class MapObject extends GameObject {

    private String name;
    private int module;
    private int mapUid;
    private int mapId;
    private int line;
    private int x;
    private int y;
    private int z;
    private int type;

}
