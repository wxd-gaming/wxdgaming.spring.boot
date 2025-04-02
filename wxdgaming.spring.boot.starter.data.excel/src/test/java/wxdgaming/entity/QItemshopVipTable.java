package wxdgaming.entity;


import lombok.Getter;
import wxdgaming.entity.bean.QItemshopVip;
import wxdgaming.spring.boot.starter.data.excel.store.DataTable;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 vip礼包, src/main/resources/范例.xlsx, q_itemshop_vip,
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-03-06 21:12:35
 **/
@Getter
public class QItemshopVipTable extends DataTable<QItemshopVip> implements Serializable {

    @Override public void initDb() {
        /*todo 实现一些数据分组*/

    }

    @Override public void checkData(Map<Class<?>, DataTable<?>> store) {
        /*todo 实现数据校验 */
    }

}