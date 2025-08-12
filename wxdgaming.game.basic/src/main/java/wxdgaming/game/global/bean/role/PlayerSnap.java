package wxdgaming.game.global.bean.role;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.bean.Vector3D;
import wxdgaming.spring.boot.batis.EntityLongUID;
import wxdgaming.spring.boot.batis.ann.DbTable;

/**
 * 玩家快照
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-12 10:53
 **/
@Getter
@Setter
@DbTable
public class PlayerSnap extends EntityLongUID {

    private int sid;
    private String account;
    private String name;
    private int level;
    private int vipLv;
    private int mapId;
    private int mapCfgId;
    private int mapLine;
    private Vector3D vector3D;
    /** 公会id */
    private long guildId;
    /** 朝向 */
    private int lastDirection;
    private int sex;
    private int job;

}
