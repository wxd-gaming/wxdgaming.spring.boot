package wxdgaming.spring.test;

import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.util.RandomUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class SkillDemo {

    public static void main(String[] args) {

        // 创建角色
        Character player = new Character("勇者", 1000, 500, 1);
        Character enemy = new Character("哥布林", 8000, 0, 1);

        // 创建技能管理器
        SkillManager skillManager = new SkillManager();

        // 为角色添加技能
        player.addSkill(skillManager.createSkill(skillManager.skillCfg1));
        player.addSkill(skillManager.createSkill(skillManager.skillCfg2));
        // 为怪物添加技能
        enemy.addSkill(skillManager.createSkill(skillManager.skillCfg1));
        enemy.addSkill(skillManager.createSkill(skillManager.skillCfg2));

        List<Character> attackList = List.of(player, enemy);

        List<Character> enemy1 = List.of(enemy, player);
        Thread.ofPlatform().start(() -> {
            while (true) {
                LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(16));
                for (Character attack : attackList) {
                    if (attack.getUseSkill() == null) {
                        boolean b = RandomUtils.randomBoolean(20);
                        if (b) {
                            Skill skill = attack.randomSkill();
                            if (skill != null) {
                                attack.setUseSkill(skill);
                                skill.use();
                                log.debug("{} 释放技能 {}", attack, skill);
                            }
                        }
                    }
                    if (attack.getUseSkill() != null) {
                        attack.getUseSkill().execute(attack, enemy1);
                        if (attack.getUseSkill().executeOver()) {
                            log.debug("{} 技能 {} 释放完毕", attack, attack.getUseSkill());
                            attack.setUseSkill(null);
                        }
                    }
                }
            }
        });
    }

}
