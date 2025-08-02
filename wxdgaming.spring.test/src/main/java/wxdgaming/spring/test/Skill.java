package wxdgaming.spring.test;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.boot.core.util.AssertUtil;

import java.util.List;

/**
 * 抽象技能模板类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-01 13:51
 */
@Slf4j
@Getter
public class Skill implements Cloneable {

    protected final SkillCfg skillCfg;
    /** 效果消耗 */
    protected final List<AbstractSkillEffect> effectList;
    /** 效果消耗 */
    protected final List<SkillCost> costs;

    private long startTime;
    private EffectExecuteIterator iterator = null;

    public Skill(SkillCfg skillCfg, List<AbstractSkillEffect> effectList, List<SkillCost> costs) {
        this.skillCfg = skillCfg;
        this.effectList = effectList;
        this.costs = costs;
    }

    @Override public String toString() {
        return skillCfg.toString();
    }

    protected void cost(Character caster) {
        for (SkillCost cost : costs) {
            switch (cost.getType()) {
                case MP:
                    AssertUtil.assertTrue(caster.getMp() >= cost.getValue(), "%s魔法值不足，无法使用%s", caster.getName(), getSkillCfg());
                    caster.setMp(caster.getMp() - cost.getValue());
                    log.debug("{} 技能{}消耗{} - {}, 当前:{}", caster, this, cost.getType(), cost.getValue(), caster.getMp());
                    break;
                case HP:
                    AssertUtil.assertTrue(caster.getHp() >= cost.getValue(), "%s生命值不足，无法使用%s", caster.getName(), getSkillCfg());
                    caster.setHp(caster.getHp() - cost.getValue());
                    log.debug("{} 技能{}消耗{} - {}, 当前:{}", caster, this, cost.getType(), cost.getValue(), caster.getHp());
                    break;
                default:
                    throw new IllegalArgumentException("未知的技能效果类型");
            }
        }
    }

    public boolean checkCD() {
        return MyClock.millis() - this.startTime > skillCfg.getCd();
    }

    public void use() {
        this.iterator = new EffectExecuteIterator(this.effectList);
        this.startTime = MyClock.millis();
    }

    public void execute(Character caster, List<Character> targets) {

        while (iterator.hasNext()) {
            AbstractSkillEffect effect = iterator.get();
            long diff = MyClock.millis() - this.startTime;
            if (effect.getExecutorDiffTime() > diff) break;
            effect.execute(caster, targets);
            iterator.moveNext();
        }

    }

    public boolean executeOver() {
        return iterator == null || !iterator.hasNext();
    }

    /***/
    private static class EffectExecuteIterator {
        private final List<AbstractSkillEffect> effectList;

        private int index = 0;

        public EffectExecuteIterator(List<AbstractSkillEffect> effectList) {
            this.effectList = effectList;
        }

        public boolean hasNext() {
            return index < effectList.size();
        }

        public AbstractSkillEffect get() {
            return effectList.get(index);
        }

        public void moveNext() {
            index++;
        }

    }

    @Override protected Skill clone() {
        try {
            Skill clone = (Skill) super.clone();
            clone.startTime = 0;
            clone.iterator = null;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
