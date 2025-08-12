package wxdgaming.entity.bean.mapping;


import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.core.lang.ObjectBase;
import wxdgaming.spring.boot.excel.store.DataKey;
import wxdgaming.spring.boot.excel.store.DataMapping;
import wxdgaming.game.bean.attr.AttrInfo;

import java.io.Serializable;

/**
 * excel 构建 怪物表, src/cfg/玩家信息.xlsx, q_player,
 *
 * @author wxd-gaming(無心道, 15388152619)
 **/
@Getter
@Setter
@DataMapping(name = "q_player", comment = "怪物表", excelPath = "src/cfg/玩家信息.xlsx", sheetName = "q_player")
public abstract class QPlayerMapping extends ObjectBase implements Serializable, DataKey {

    /** 主键id/等级 */
    protected int id;
    /** 升级所需要的经验值 */
    protected int exp;
    /** 属性 */
    protected AttrInfo attr;
    /** 属性 */
    protected AttrInfo attrPro;

    public Object key() {
        return id;
    }

}
