package wxdgaming.spring.test.skill;

import lombok.Builder;
import lombok.Getter;
import wxdgaming.spring.boot.core.timer.MyClock;
import wxdgaming.spring.test.map.MapObject;

import java.util.List;

/**
 * 技能执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-02 22:00
 **/
@Getter
public class SkillExecutor {

    private final long uid;
    private final Skill skill;
    private final MapObject self;
    private final List<MapObject> targets;
    private final long startTime;
    private int index;

    @Builder
    public SkillExecutor(MapObject self, Skill skill, List<MapObject> targets, long uid) {
        this.self = self;
        this.skill = skill;
        this.targets = targets;
        this.uid = uid;
        this.startTime = MyClock.millis();
    }

    public boolean hasNext() {
        return index < skill.getEffectList().size();
    }

    public SkillEffect get() {
        return skill.getEffectList().get(index);
    }

    public void moveNext() {
        index++;
    }

    @Override public String toString() {
        return "SkillExecutor{uid=%d, skill=%s}".formatted(uid, skill);
    }
}
