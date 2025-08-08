package wxdgaming.spring.logcenter.bean;

import com.alibaba.fastjson.serializer.SerializerFeature;
import wxdgaming.spring.boot.batis.convert.Converter;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;

import java.lang.reflect.Type;

/**
 * jsonb
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-08 10:41
 **/
public class JsonbConvert extends Converter<Object, String> {

    @Override public String toDb(Object o) {
        return FastJsonUtil.toJSONString(o, SerializerFeature.SortField, SerializerFeature.MapSortField, SerializerFeature.WriteNonStringKeyAsString);
    }

    @Override public Object fromDb(Type type, String json) {
        return FastJsonUtil.parse(json, type);
    }

}
