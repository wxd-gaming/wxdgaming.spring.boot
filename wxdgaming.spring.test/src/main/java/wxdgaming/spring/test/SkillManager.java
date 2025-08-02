package wxdgaming.spring.test;


import wxdgaming.spring.test.impl.SkillCostHpEffect;
import wxdgaming.spring.test.impl.SkillHealHpEffect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 技能管理器
public class SkillManager {

    private final Map<Integer, Skill> skillTemplates;

    public SkillManager() {
        skillTemplates = new ConcurrentHashMap<>();
        initializeSkillTemplates();
    }

    SkillCfg skillCfg1 = new SkillCfg().setId(1).setLv(1).setName("治愈术").setCd(15000);
    SkillCfg skillCfg2 = new SkillCfg().setId(2).setLv(1).setName("火球术").setCd(5000);

    private void initializeSkillTemplates() {
        Skill healSkill = new Skill(
                skillCfg1,
                List.of(new SkillHealHpEffect(skillCfg1, TargetType.ALL_ALLIES, List.of())),
                List.of(new SkillCost(SkillCost.SkillCostType.MP, 5))
        );
        skillTemplates.put(healSkill.getSkillCfg().getId(), healSkill);
        AbstractSkillEffect e1 = new SkillCostHpEffect(skillCfg2, TargetType.ALL_ENEMIES, List.of()).setExecutorDiffTime(1000);
        AbstractSkillEffect e2 = new SkillHealHpEffect(skillCfg2, TargetType.ALL_ALLIES, List.of()).setExecutorDiffTime(3000);
        Skill fireballSkill = new Skill(
                skillCfg2,
                List.of(e1, e2),
                List.of(new SkillCost(SkillCost.SkillCostType.MP, 5))
        );
        skillTemplates.put(fireballSkill.getSkillCfg().getId(), fireballSkill);
    }

    public Skill createSkill(SkillCfg skillCfg) {
        Skill template = skillTemplates.get(skillCfg.getId());
        if (template != null) {
            // 这里可以添加复制逻辑，创建新实例
            return template.clone();
        }
        return null;
    }

}
