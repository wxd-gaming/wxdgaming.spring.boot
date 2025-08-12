package wxdgaming.game.server.script.role.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.bean.goods.BagChangeArgs4ItemCfg;
import wxdgaming.game.bean.goods.ItemCfg;
import wxdgaming.game.core.Reason;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.event.OnCreateRole;
import wxdgaming.game.server.script.bag.BagService;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色创建事件
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 19:51
 **/
@Slf4j
@Component
public class PlayerCreateHandler {

    final BagService bagService;

    public PlayerCreateHandler(BagService bagService) {
        this.bagService = bagService;
    }

    /** 创建角色之后赠送初始化道具 */
    @OnCreateRole
    public void onCreateRoleInitGoods(Player player) {
        log.info("角色创建:{}", player);
        ItemCfg.ItemCfgBuilder builder = ItemCfg.builder();
        List<ItemCfg> rewards = new ArrayList<>();
        rewards.add(builder.cfgId(1).num(10000).build());
        rewards.add(builder.cfgId(2).num(10000).build());
        rewards.add(builder.cfgId(3).num(100000).build());
        rewards.add(builder.cfgId(4).num(100000).build());
        rewards.add(builder.cfgId(5).num(1).build());
        ReasonArgs reasonArgs = ReasonArgs.of(Reason.CreateRole);
        BagChangeArgs4ItemCfg rewardArgs4ItemCfg = BagChangeArgs4ItemCfg.builder()
                .setItemCfgList(rewards)
                .setReasonArgs(reasonArgs)
                .build();
        bagService.gainItemCfg(player, rewardArgs4ItemCfg);
    }

}
