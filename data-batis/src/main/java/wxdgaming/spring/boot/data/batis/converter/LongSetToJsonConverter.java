package wxdgaming.spring.boot.data.batis.converter;

import com.alibaba.fastjson.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import wxdgaming.spring.boot.core.json.FastJsonUtil;

import java.util.HashSet;

/**
 * 把 HashSet<Long> 转换成 json
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-08 09:17
 **/
@Converter
public class LongSetToJsonConverter implements AttributeConverter<HashSet<Long>, String> {

    @Override public String convertToDatabaseColumn(HashSet<Long> attribute) {
        return FastJsonUtil.toJsonWriteType(attribute);
    }

    @Override public HashSet<Long> convertToEntityAttribute(String dbData) {
        return FastJsonUtil.parse(dbData, new TypeReference<HashSet<Long>>(HashSet.class, Long.class) {});
    }

}
