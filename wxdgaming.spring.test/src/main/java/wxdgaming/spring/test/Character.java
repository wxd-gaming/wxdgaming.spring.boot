package wxdgaming.spring.test;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.core.util.RandomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 角色类定义
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-30 19:08
 */
@Getter
@Setter
public class Character {

    private String name;
    private int hp;
    private int mp;
    private int level;
    private List<Skill> skills;
    private Skill useSkill;

    public Character(String name, int hp, int mp, int level) {
        this.name = name;
        this.hp = hp;
        this.mp = mp;
        this.level = level;
        this.skills = new ArrayList<>();
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public Skill randomSkill() {
        List<Skill> list = skills.stream().filter(Skill::checkCD).toList();
        if (list.isEmpty()) return null;
        return RandomUtils.randomItem(list);
    }

    @Override public String toString() {
        return "Character{name='%s'}".formatted(name);
    }
}
