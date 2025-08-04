package wxdgaming.spring.test.skill;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Autowired;
import wxdgaming.spring.test.map.MapObject;
import wxdgaming.spring.test.map.MapObjectService;

import java.util.List;

/**
 * 技能效果
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-31 10:46
 **/
@Getter
@Setter
@Accessors(chain = true)
public abstract class AbstractSkillEffectExecutor {

    @Autowired MapObjectService mapObjectService;

    public abstract SkillEffectType skillEffectType();

    public final void execute(MapObject self, Skill skill, SkillEffect skillEffect, List<MapObject> targets) {
        for (MapObject target : targets) {
            onExecute(self, skill, skillEffect, target);
        }
    }

    protected abstract void onExecute(MapObject self, Skill skill, SkillEffect skillEffect, MapObject target);

}
