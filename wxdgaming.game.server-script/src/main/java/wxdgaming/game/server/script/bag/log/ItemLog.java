package wxdgaming.game.server.script.bag.log;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.slog.AbstractRoleLog;

/**
 * 背包日志
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 19:16
 **/
@Getter
@Setter
@NoArgsConstructor
public class ItemLog extends AbstractRoleLog {

    private String bagType;
    private String changeType;
    private int itemCfgId;
    private String itemName;
    private long oldNum;
    private long change;
    private long newNum;
    private String reason;

    public ItemLog(Player player, String bagType, String changeType, int itemCfgId, String itemName, long oldNum, long change, long newNum, String reason) {
        super(player);
        this.bagType = bagType;
        this.changeType = changeType;
        this.itemCfgId = itemCfgId;
        this.itemName = itemName;
        this.oldNum = oldNum;
        this.change = change;
        this.newNum = newNum;
        this.reason = reason;
    }

    @Override public String logType() {
        return "itemlog";
    }

}
