package wxdgaming.game.server.script.bag.cost.impl.currency;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.cfg.bean.QItem;
import wxdgaming.game.server.bean.bag.ItemGrid;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.bean.bag.BagChangesCourse;
import wxdgaming.game.server.script.bag.cost.CostScript;

/**
 * 货币的扣除
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-23 17:35
 **/
@Slf4j
@Component
public class CurrencyCostScript extends CostScript {

    @Override public ItemTypeConst type() {
        return ItemTypeConst.CurrencyType;
    }

    @Override public void cost(Player player, BagChangesCourse bagChangesCourse, QItem qItem, long count) {
        int cfgId = qItem.getId();
        bagChangesCourse.subtractCurrency(cfgId, count);
    }

    @Override public void cost(Player player, BagChangesCourse bagChangesCourse, ItemGrid itemGrid, long count) {
        cost(player, bagChangesCourse, itemGrid.getItem().qItem(), count);
    }
}
