package wxdgaming.spring.boot.starter.batis.converter;

import com.alibaba.fastjson.TypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import wxdgaming.spring.boot.starter.core.json.FastJsonUtil;

/**
 * 通用的 json 转换器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-10 20:36
 */
@Converter
public class ObjectToJsonNodeConverter implements AttributeConverter<Object, JsonNode> {
    @Override public JsonNode convertToDatabaseColumn(Object attribute) {
        String jsonWriteType = FastJsonUtil.toJSONStringAsWriteType(attribute);
        try {
            return new ObjectMapper().readTree(jsonWriteType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public Object convertToEntityAttribute(JsonNode dbData) {
        String string = dbData.toString();
        return FastJsonUtil.parse(string, new TypeReference<>() {});
    }
}
