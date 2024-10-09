package wxdgaming.spring.boot.core.lang;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import wxdgaming.spring.boot.core.util.StringsUtil;

/**
 * 配置字符串
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-08 20:44
 **/
public class ConfigString extends ObjectBase {

    @Getter @JSONField(ordinal = 1)
    String value;
    private transient Integer integer = null;
    private transient int[] ints = null;

    @JSONCreator
    public ConfigString(@JSONField(name = "value") String value) {
        this.value = value;
    }

    public Integer integer() {
        if (StringsUtil.emptyOrNull(value)) return null;
        if (integer == null) {
            integer = Integer.parseInt(value);
        }
        return integer;
    }

    public int intVal() {
        Integer tmp = integer();
        if (tmp == null) return 0;
        return tmp;
    }

    public int[] ints() {
        if (StringsUtil.emptyOrNull(value)) return null;
        if (ints == null) {
            // ints = StringsUtil.splitInts(value);
        }
        return ints;
    }

}
