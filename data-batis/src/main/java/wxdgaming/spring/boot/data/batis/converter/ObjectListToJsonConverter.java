package wxdgaming.spring.boot.data.batis.converter;

import com.alibaba.fastjson.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import wxdgaming.spring.boot.core.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 把 ArrayList<Object> 转换成 json
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-08 09:17
 **/
@Converter
public class ObjectListToJsonConverter implements AttributeConverter<List<Object>, String> {

    @Override public String convertToDatabaseColumn(List<Object> attribute) {
        return FastJsonUtil.toJsonWriteType(attribute);
    }

    @Override public ArrayList<Object> convertToEntityAttribute(String dbData) {
        return FastJsonUtil.parse(dbData, new TypeReference<ArrayList<Object>>(ArrayList.class, Object.class) {});
    }

}
