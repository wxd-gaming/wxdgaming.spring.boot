package wxdgaming.game.server.script.fight;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.ann.Init;
import wxdgaming.spring.boot.core.util.AssertUtil;
import wxdgaming.game.core.ReasonArgs;
import wxdgaming.game.server.bean.MapNpc;

import java.util.HashMap;
import java.util.List;

/**
 * 战斗
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-08 10:16
 **/
@Slf4j
@Service
public class FightService extends HoldRunApplication {

    HashMap<Integer, AbstractFightAction> actionImplMap = new HashMap<>();

    @Init
    public void init() {
        HashMap<Integer, AbstractFightAction> map = new HashMap<>();
        runApplication.classWithSuper(AbstractFightAction.class)
                .forEach(impl -> {
                    AbstractFightAction old = map.put(impl.type(), impl);
                    AssertUtil.assertTrue(old == null, "重复的战斗动作类型" + impl.type());
                });
        actionImplMap = map;
    }

    public void changeHp(MapNpc mapNpc, long change, ReasonArgs reasonArgs) {
        long oldHp = mapNpc.getHp();
        long maxHp = mapNpc.maxHp();
        if (oldHp >= maxHp) {
            return;
        }
        mapNpc.setHp(oldHp + change);
        if (mapNpc.getHp() > maxHp) {
            mapNpc.setHp(maxHp);
        }
        if (mapNpc.getHp() < 0) {
            mapNpc.setHp(0);
        }
        log.info("{} 改变血量 {} -> {} -> {}, maxMp={}, {}", mapNpc, oldHp, change, mapNpc.getHp(), maxHp, reasonArgs);
    }

    public void changeMp(MapNpc player, long change, ReasonArgs reasonArgs) {
        long oldMp = player.getMp();
        long maxMp = player.maxMp();
        if (oldMp >= maxMp) {
            return;
        }
        player.setMp(oldMp + change);
        if (player.getMp() > maxMp) {
            player.setMp(maxMp);
        }
        if (player.getMp() < 0) {
            player.setMp(0);
        }
        log.info("{} 改变魔量 {} -> {} -> {}, maxMp={}, {}", player, oldMp, change, player.getMp(), maxMp, reasonArgs);
    }

    public List<MapNpc> selectAttack(MapNpc player) {
        return null;
    }

}
