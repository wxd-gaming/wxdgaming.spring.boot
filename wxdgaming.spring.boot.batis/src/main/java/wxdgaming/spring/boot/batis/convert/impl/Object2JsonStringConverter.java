package wxdgaming.spring.boot.batis.convert.impl;


import wxdgaming.spring.boot.batis.convert.Converter;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;

import java.lang.reflect.Type;

/**
 * 任意对象转化成字节数组
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-16 10:02
 **/
public class Object2JsonStringConverter extends Converter<Object, String> {

    @Override public String toDb(Object o) {
        return FastJsonUtil.toJSONString(o);
    }

    @Override public Object fromDb(Type type, String json) {
        return FastJsonUtil.parse(json, type);
    }

}
