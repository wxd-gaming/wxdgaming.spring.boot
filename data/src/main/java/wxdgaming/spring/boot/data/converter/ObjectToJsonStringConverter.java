package wxdgaming.spring.boot.data.converter;

import com.alibaba.fastjson.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import wxdgaming.spring.boot.core.json.FastJsonUtil;

/**
 * 通用的 json 转换器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-10 20:36
 */
@Converter
public class ObjectToJsonStringConverter implements AttributeConverter<Object, String> {
    @Override public String convertToDatabaseColumn(Object attribute) {
        return FastJsonUtil.toJsonWriteType(attribute);
    }

    @Override public Object convertToEntityAttribute(String dbData) {
        return FastJsonUtil.parse(dbData, new TypeReference<>() {});
    }
}
