package wxdgaming.game.server.bean.role;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.batis.ColumnType;
import wxdgaming.spring.boot.batis.EntityLongUID;
import wxdgaming.spring.boot.batis.ann.DbColumn;
import wxdgaming.spring.boot.batis.ann.DbTable;

/**
 * 角色数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-30 14:43
 **/
@Getter
@Setter
@Accessors(chain = true)
@DbTable(tableName = "role")
public class RoleEntity extends EntityLongUID {

    private long lastUpdateTime;
    @DbColumn(index = true)
    int sid;
    @DbColumn(index = true, length = 18)
    String name;
    @DbColumn(index = true)
    int lv;
    @DbColumn(index = true)
    int vipLv;
    @DbColumn(index = true, length = 64)
    String account;
    @DbColumn(index = true)
    boolean del;
    /** 最好登录时间 */
    @DbColumn(index = true)
    long lastLoginTime;
    /** 最好退出登录时间 */
    @DbColumn(index = true)
    long lastLogoutTime;
    /** 在线毫秒数 */
    @DbColumn(index = true)
    long totalOnlineMills;
    /** 角色数据 */
    @DbColumn(length = Integer.MAX_VALUE, columnType = ColumnType.String)
    private Player player;

    @Override public void saveRefresh() {
        lastUpdateTime = System.currentTimeMillis();
        if (getUid() == 0) {
            setUid(player.getUid());
        }
        sid = player.getSid();
        name = player.getName();
        account = player.getAccount();
        del = player.isDel();
        lv = player.getLevel();
        lastLoginTime = player.getOnlineInfo().getLastLoginTime();
        lastLogoutTime = player.getOnlineInfo().getLastLogoutTime();
        totalOnlineMills = player.getOnlineInfo().getOnlineTotalMills();
    }

    @Override public RoleEntity setUid(long uid) {
        super.setUid(uid);
        return this;
    }

}
