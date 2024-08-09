package wxdgaming.spring.boot.data.excel;

import com.alibaba.fastjson.JSONObject;
import lombok.*;
import lombok.experimental.Accessors;

import java.util.LinkedHashMap;

/**
 * 表信息
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 20:49
 **/
@Getter
@Setter
@Accessors(chain = true)
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TableInfo {

    private String filePath;
    private String fileName;
    private String sheetName;
    private String tableName;
    private String tableComment;
    private LinkedHashMap<String, CellInfo> cellInfoMap = new LinkedHashMap<>();
    private LinkedHashMap<Object, JSONObject> rows = new LinkedHashMap<>();

}
