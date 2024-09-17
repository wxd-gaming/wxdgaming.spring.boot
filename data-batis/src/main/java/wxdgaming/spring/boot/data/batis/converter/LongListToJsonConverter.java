package wxdgaming.spring.boot.data.batis.converter;

import com.alibaba.fastjson.TypeReference;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import wxdgaming.spring.boot.core.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 把 List<Long> 转换成 json
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-08 09:17
 **/
@Converter
public class LongListToJsonConverter implements AttributeConverter<List<Long>, String> {

    @Override public String convertToDatabaseColumn(List<Long> attribute) {
        return FastJsonUtil.toJsonWriteType(attribute);
    }

    @Override public List<Long> convertToEntityAttribute(String dbData) {
        return FastJsonUtil.parse(dbData, new TypeReference<ArrayList<Long>>(ArrayList.class, Long.class) {});
    }

}
