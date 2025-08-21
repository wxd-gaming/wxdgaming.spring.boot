package wxdgaming.spring.logserver.bean;

import lombok.Getter;
import lombok.Setter;
import wxdgaming.boot2.core.lang.ObjectBase;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 日志映射信息
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 19:46
 **/
@Getter
@Setter
public class LogMappingInfo extends ObjectBase {

    private String group = "";
    private String sort = "1";
    private String logName;
    /** 表注释 */
    private String logComment;
    /** 路由 */
    private String routing = "/log-table.html";
    private String htmlStyle;
    /** 是否开启分区 是按照每天进行区分 */
    private boolean partition;
    private List<LogField> fieldList = new ArrayList<>();

    public Function<String, Object> fieldValueFunction(String fieldName) {
        return fieldList.stream()
                .filter(logField -> logField.getFieldName().equals(fieldName))
                .findFirst()
                .map(logField -> switch (logField.getFieldType()) {
                    case "int" -> (Function<String, Object>) Integer::valueOf;
                    case "long" -> (Function<String, Object>) Long::valueOf;
                    case "float" -> (Function<String, Object>) Float::valueOf;
                    case "double" -> (Function<String, Object>) Double::valueOf;
                    case "boolean" -> (Function<String, Object>) Boolean::valueOf;
                    default -> (Function<String, Object>) String::valueOf;
                })
                .orElse(null);
    }

    public LogMappingInfo setLogName(String logName) {
        this.logName = logName.toLowerCase();
        return this;
    }

}
