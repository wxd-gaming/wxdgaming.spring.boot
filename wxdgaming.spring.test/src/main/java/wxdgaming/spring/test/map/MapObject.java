package wxdgaming.spring.test.map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.util.RandomUtils;
import wxdgaming.spring.test.GameObject;
import wxdgaming.spring.test.buff.Buff;
import wxdgaming.spring.test.skill.Skill;
import wxdgaming.spring.test.skill.SkillExecutor;

import java.util.ArrayList;
import java.util.List;

/**
 * 地图对象
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-02 21:37
 **/
@Getter
@Setter
@Accessors(chain = true)
public class MapObject extends GameObject {

    private MapObjectType objectType;
    private long hp;
    private long mp;
    private int level;
    private List<Skill> skills = new ArrayList<>();
    private List<Buff> buffs = new ArrayList<>();
    private SkillExecutor useSkill;

    @Override public MapObject setUid(long uid) {
        super.setUid(uid);
        return this;
    }

    @Override public MapObject setName(String name) {
        super.setName(name);
        return this;
    }

    public long maxHp() {
        return 10000;
    }

    public long maxMp() {
        return 5000;
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public Skill randomSkill() {
        List<Skill> list = skills.stream()
                .filter(Skill::checkCD)
                .filter(skill -> skill.checkCost(this))
                .toList();
        if (list.isEmpty()) return null;
        return RandomUtils.randomItem(list);
    }

    @Override public String toString() {
        return "MapObject{uid=%d, name='%s', level=%d}".formatted(getUid(), getName(), level);
    }
}
