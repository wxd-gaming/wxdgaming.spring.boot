package wxdgaming.spring.boot.starter.batis.converter;

import com.alibaba.fastjson.JSONObject;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import wxdgaming.spring.boot.starter.core.json.FastJsonUtil;

/**
 * 把 object 转换成 json
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-08 09:17
 **/
@Converter
public class JSONObjectToJsonConverter implements AttributeConverter<JSONObject, String> {

    @Override public String convertToDatabaseColumn(JSONObject attribute) {
        return FastJsonUtil.toJSONStringAsWriteType(attribute);
    }

    @Override public JSONObject convertToEntityAttribute(String dbData) {
        return FastJsonUtil.parse(dbData);
    }

}
