package wxdgaming.spring.boot.excel;

import com.alibaba.fastjson.JSONObject;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.util.ConvertUtil;

/**
 * excel 一行数据
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-08-09 20:17
 */
public class RowData extends JSONObject {

    public RowData() {
        this(true);
    }

    public RowData(boolean ordered) {
        super(ordered);
    }

    @Override public String getString(String key) {
        Object object = super.get(key);
        if (object == null) {
            return null;
        } else if (ConvertUtil.isBaseType(object.getClass())) {
            return String.valueOf(object);
        } else {
            return FastJsonUtil.toJSONString(object);
        }
    }

}
