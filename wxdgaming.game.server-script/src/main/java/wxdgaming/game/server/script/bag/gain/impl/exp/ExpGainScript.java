package wxdgaming.game.server.script.bag.gain.impl.exp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.bag.BagChangesCourse;
import wxdgaming.game.server.bean.bag.ItemBag;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.script.bag.gain.impl.currency.CurrencyGainScript;

/**
 * 货币的获得
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-23 17:35
 **/
@Slf4j
@Component
public class ExpGainScript extends CurrencyGainScript {

    @Override public ItemTypeConst type() {
        return ItemTypeConst.EXP;
    }

    @Override public long getCount(Player player, ItemBag itemBag, int cfgId) {
        return player.getExp();
    }

    @Override public boolean gain(BagChangesCourse bagChangesCourse, Item newItem) {
        long count = newItem.getCount();
        playerService.addExp(bagChangesCourse.getPlayer(), count, bagChangesCourse.getReasonArgs());
        return true;
    }

}
