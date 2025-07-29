package wxdgaming.spring.boot.batis;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.batis.ann.DbColumn;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.lang.ObjectBase;

/**
 * 实体类基类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-16 01:25
 **/
@Getter
@Setter
public abstract class Entity extends ObjectBase {

    /** null 未知， true 表示不存在数据库 false 表示在数据库 */
    @DbColumn(ignore = true)
    @JSONField(serialize = false, deserialize = false)
    private transient Boolean newEntity = null;
    @DbColumn(ignore = true)
    @JSONField(serialize = false, deserialize = false)
    private transient int oldHashCode = -1;

    public boolean checkHashCode() {
        String jsonString = FastJsonUtil.toJSONString(
                this,
                SerializerFeature.SortField,   /*排序*/
                SerializerFeature.MapSortField
        );
        int hashcode = StringUtils.hashcode(jsonString);
        if (hashcode != oldHashCode) {
            oldHashCode = hashcode;
            return true;
        }
        return false;
    }

    /** 保存到数据库 */
    public void saveRefresh() {}

}
