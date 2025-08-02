package wxdgaming.spring.test;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 技能配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-08-01 15:09
 **/
@Getter
@Setter
@Accessors(chain = true)
public class SkillCfg {

    private int id;
    private int lv;
    private String name;
    private long cd;

    @Override public String toString() {
        return "SkillCfg{id=%d, lv=%d, name='%s'}".formatted(id, lv, name);
    }

}
