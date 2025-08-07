package wxdgaming.spring.test.skill;

import lombok.Builder;
import lombok.Getter;

/**
 * 技能配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-01 15:09
 **/
@Getter
@Builder
public class SkillCfg {

    private int id;
    private int lv;
    private String name;
    private long cd;

    @Override public String toString() {
        return "SkillCfg{id=%d, name='%s', lv=%d}".formatted(id, name, lv);
    }

}
