package wxdgaming.game.server.script.bag.gain.impl.equip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.bean.goods.Equipment;
import wxdgaming.game.bean.goods.Item;
import wxdgaming.game.bean.goods.ItemTypeConst;
import wxdgaming.game.server.bean.bag.BagChangesCourse;
import wxdgaming.game.server.bean.bag.ItemBag;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.script.bag.BagService;
import wxdgaming.game.server.script.bag.gain.GainScript;
import wxdgaming.game.server.script.role.PlayerService;

/**
 * 获得道具
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-22 19:13
 **/
@Slf4j
@Component
public class EquipGainScript extends GainScript {

    protected final DataCenterService dataCenterService;
    protected final PlayerService playerService;
    protected final BagService bagService;

    public EquipGainScript(DataCenterService dataCenterService, PlayerService playerService, BagService bagService) {
        this.dataCenterService = dataCenterService;
        this.playerService = playerService;
        this.bagService = bagService;
    }


    public ItemTypeConst type() {
        return ItemTypeConst.EquipType;
    }

    @SuppressWarnings("unchecked")
    @Override protected <T extends Item> T newItem() {
        return (T) new Equipment();
    }

    @Override protected void initItem(Item item) {
        super.initItem(item);
    }

    @Override public boolean gain(BagChangesCourse bagChangesCourse, Item newItem) {
        return super.gain(bagChangesCourse, newItem);
    }

    @Override public long getCount(Player player, ItemBag itemBag, int cfgId) {
        return super.getCount(player, itemBag, cfgId);
    }

}
