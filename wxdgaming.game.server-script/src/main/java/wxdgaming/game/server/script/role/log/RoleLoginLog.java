package wxdgaming.game.server.script.role.log;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.log.AbstractRoleLog;

/**
 * 角色登录日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 19:46
 **/
@Getter
@Setter
public class RoleLoginLog extends AbstractRoleLog {

    private String ip;
    private String clientData;

    public RoleLoginLog(Player player, String ip, String clientData) {
        super(player);
        this.ip = ip;
        this.clientData = clientData;
    }

    @Override public String logType() {
        return "roleloginlog";
    }

}
