package wxdgaming.spring.test.skill;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.test.TargetGroup;
import wxdgaming.spring.test.skill.impl.SkillCostHpEffect;
import wxdgaming.spring.test.skill.impl.SkillHealHpEffect;
import wxdgaming.spring.test.skill.impl.SkillHealMpEffect;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 技能服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-02 21:29
 */
@Slf4j
@Service
public class SkillService implements InitPrint {

    private final Map<Integer, Skill> skillTemplates = new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeSkillTemplates() {

        {
            SkillCfg skillCfg1 = SkillCfg.builder().id(1).lv(1).name("治愈术").cd(15000).build();
            AbstractSkillEffect healHpEffect = new SkillHealHpEffect(skillCfg1, TargetGroup.Friend, 1);
            AbstractSkillEffect healMpEffect = new SkillHealMpEffect(skillCfg1, TargetGroup.Friend, 1);

            Skill healSkill = Skill.builder()
                    .skillCfg(skillCfg1)
                    .effectList(List.of(healHpEffect, healMpEffect))
                    .costs(List.of(new SkillCost(SkillCost.SkillCostType.MP, 5)))
                    .build();
            skillTemplates.put(healSkill.getSkillCfg().getId(), healSkill);
        }

        {
            SkillCfg skillCfg2 = SkillCfg.builder().id(2).lv(1).name("全体治愈术").cd(15000).build();
            AbstractSkillEffect healHpEffect = new SkillHealHpEffect(skillCfg2, TargetGroup.Friend, Integer.MAX_VALUE);
            AbstractSkillEffect healMpEffect = new SkillHealMpEffect(skillCfg2, TargetGroup.Friend, Integer.MAX_VALUE);

            Skill healSkill = Skill.builder()
                    .skillCfg(skillCfg2)
                    .effectList(List.of(healHpEffect, healMpEffect))
                    .costs(List.of(new SkillCost(SkillCost.SkillCostType.MP, 50)))
                    .build();
            skillTemplates.put(healSkill.getSkillCfg().getId(), healSkill);
        }

        {
            SkillCfg skillCfg3 = SkillCfg.builder().id(3).lv(1).name("火球术").cd(5000).build();

            AbstractSkillEffect e1 = new SkillCostHpEffect(skillCfg3, TargetGroup.Enemy, 1).setExecutorDiffTime(1000);
            Skill fireballSkill = Skill.builder()
                    .skillCfg(skillCfg3)
                    .effectList(List.of(e1))
                    .costs(List.of(new SkillCost(SkillCost.SkillCostType.MP, 5)))
                    .build();
            skillTemplates.put(fireballSkill.getSkillCfg().getId(), fireballSkill);
        }

        {
            SkillCfg skillCfg4 = SkillCfg.builder().id(4).lv(1).name("地火雨").cd(5000).build();

            AbstractSkillEffect e1 = new SkillCostHpEffect(skillCfg4, TargetGroup.Enemy, Integer.MAX_VALUE).setExecutorDiffTime(1000);
            Skill fireballSkill = Skill.builder()
                    .skillCfg(skillCfg4)
                    .effectList(List.of(e1))
                    .costs(List.of(new SkillCost(SkillCost.SkillCostType.MP, 50)))
                    .build();
            skillTemplates.put(fireballSkill.getSkillCfg().getId(), fireballSkill);
        }

    }

    public Skill createSkill(int cfgId) {
        Skill template = skillTemplates.get(cfgId);
        if (template != null) {
            // 这里可以添加复制逻辑，创建新实例
            return template;
        }
        return null;
    }

}
