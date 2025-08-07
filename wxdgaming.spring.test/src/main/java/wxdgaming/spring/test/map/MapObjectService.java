package wxdgaming.spring.test.map;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.util.RandomUtils;
import wxdgaming.spring.test.TargetGroup;
import wxdgaming.spring.test.skill.SkillService;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 管理器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-02 21:13
 **/
@Slf4j
@Getter
@Service
public class MapObjectService implements InitPrint {

    /** 场景对象 */
    private final ConcurrentHashMap<Long, MapObject> mapObjectMap = new ConcurrentHashMap<>();
    private final SkillService skillService;

    @Autowired
    public MapObjectService(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostConstruct()
    public void init() {
        {
            MapObject player1 = new MapObject().setObjectType(MapObjectType.Player).setUid(1).setName("萨满").setHp(1000).setMp(500).setLevel(1);
            player1.addSkill(skillService.createSkill(1));
            player1.addSkill(skillService.createSkill(2));
            mapObjectMap.put(player1.getUid(), player1);
        }

        {
            MapObject player2 = new MapObject().setObjectType(MapObjectType.Player).setUid(2).setName("MT").setHp(1000).setMp(500).setLevel(1);
            player2.addSkill(skillService.createSkill(3));
            player2.addSkill(skillService.createSkill(4));
            mapObjectMap.put(player2.getUid(), player2);
        }

        {
            MapObject enemy1 = new MapObject().setObjectType(MapObjectType.Monster).setUid(21).setName("哥布林").setHp(1000).setMp(500).setLevel(1);
            enemy1.addSkill(skillService.createSkill(3));
            enemy1.addSkill(skillService.createSkill(4));
            mapObjectMap.put(enemy1.getUid(), enemy1);
        }
        {
            MapObject enemy2 = new MapObject().setObjectType(MapObjectType.Monster).setUid(22).setName("小软").setHp(1000).setMp(500).setLevel(1);
            enemy2.addSkill(skillService.createSkill(3));
            enemy2.addSkill(skillService.createSkill(4));
            mapObjectMap.put(enemy2.getUid(), enemy2);
        }
        {
            MapObject enemy2 = new MapObject().setObjectType(MapObjectType.Monster).setUid(22).setName("巫师").setHp(1000).setMp(500).setLevel(1);
            enemy2.addSkill(skillService.createSkill(1));
            enemy2.addSkill(skillService.createSkill(2));
            mapObjectMap.put(enemy2.getUid(), enemy2);
        }
    }

    public List<MapObject> filterTargets(MapObject self, List<MapObject> targets, TargetGroup targetType) {
        return switch (targetType) {
            case Self -> List.of(self);
            case Target -> targets;
            case All -> mapObjectMap.values().stream()
                    .filter(target -> !isDead(target))
                    .collect(Collectors.toList());
            case Friend -> targets.stream()
                    .filter(target -> !isDead(target))
                    .filter(target -> target.getObjectType() == self.getObjectType())
                    .toList();
            case Enemy -> targets.stream()
                    .filter(target -> !isDead(target))
                    .filter(target -> target.getObjectType() != self.getObjectType())
                    .toList();
            case Team -> targets.stream()
                    .filter(target -> !isDead(target))
                    .filter(target -> target.getObjectType() == self.getObjectType())
                    .toList();
            default -> List.of();
        };
    }

    public List<MapObject> findTargets(MapObject self, TargetGroup targetType, int targetCount) {
        List<MapObject> targets = switch (targetType) {
            case Self -> List.of(self);
            case All -> mapObjectMap.values().stream()
                    .filter(target -> !isDead(target))
                    .collect(Collectors.toList());
            case Friend -> mapObjectMap.values().stream()
                    .filter(target -> !isDead(target))
                    .filter(target -> target.getObjectType() == self.getObjectType())
                    .collect(Collectors.toList());
            case Enemy -> mapObjectMap.values().stream()
                    .filter(target -> !isDead(target))
                    .filter(target -> target.getObjectType() != self.getObjectType())
                    .collect(Collectors.toList());
            case Team -> mapObjectMap.values().stream()
                    .filter(target -> !isDead(target))
                    .filter(target -> target.getObjectType() == self.getObjectType())
                    .collect(Collectors.toList());
            default -> List.of();
        };

        if (targets.size() > targetCount) {
            for (int i = 0; i < targetCount; i++) {
                RandomUtils.randomRemove(targets);
            }
        }
        return targets;
    }

    public void costHp(MapObject self, long change) {
        self.setHp(self.getHp() - change);
        if (self.getHp() < 0) {
            self.setHp(0);
        }
        if (isDead(self)) {
            log.debug("{}死亡", self, new RuntimeException(","));
        }
    }

    public void costMp(MapObject self, long change) {
        self.setMp(self.getMp() - change);
        if (self.getMp() < 0) {
            self.setMp(0);
        }
    }

    public void healHp(MapObject self, long change) {
        self.setHp(self.getHp() + change);
        if (self.getHp() > self.maxHp()) {
            self.setHp(self.maxHp());
        }
    }

    public void healMp(MapObject self, long change) {
        self.setMp(self.getMp() + change);
        if (self.getMp() > self.maxMp()) {
            self.setMp(self.maxMp());
        }
    }


    public boolean isDead(MapObject self) {
        return self.getHp() <= 0;
    }

}
