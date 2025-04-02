package wxdgaming.spring.boot.starter.data.excel.store;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import wxdgaming.spring.boot.starter.core.format.TemplatePack;
import wxdgaming.spring.boot.starter.core.io.FileUtil;
import wxdgaming.spring.boot.starter.core.util.StringUtils;
import wxdgaming.spring.boot.starter.data.excel.CellInfo;
import wxdgaming.spring.boot.starter.data.excel.TableData;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

/**
 * excel to java code
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-08 19:49
 **/
@Slf4j
public class CreateJavaCode implements ICreateCode {

    @Getter private static final CreateJavaCode ins = new CreateJavaCode();

    CreateJavaCode() {}

    @Override public void createCode(TableData tableData, String outPath, String packageName, String belong) {
        if (!outPath.endsWith("/")) {
            outPath += "/";
        }
        JSONObject parse = new JSONObject();
        parse.put("packageName", packageName);
        parse.put("tableName", tableData.getTableName());
        parse.put("tableComment", tableData.getTableComment());
        parse.put("filePath", tableData.getFilePath());
        parse.put("codeClassName", tableData.getCodeClassName());
        if (tableData.getCellInfo4IndexMap().values().stream().anyMatch(ci -> ci.getFieldName().equals("q_id"))) {
            parse.put("keyColumn", "q_id");
        } else if (tableData.getCellInfo4IndexMap().values().stream().anyMatch(ci -> ci.getFieldName().equals("id"))) {
            parse.put("keyColumn", "id");
        } else {
            throw new RuntimeException("未找到主键列 q_id or id");
        }
        ArrayList<Map<String, Object>> columns = new ArrayList<>();
        for (CellInfo cellInfo : tableData.getCellInfo4IndexMap().values()) {
            if (!StringUtils.isBlank(belong) && !cellInfo.getFieldBelong().equals(belong)) continue;
            Map<String, Object> column = new JSONObject();
            column.put("fieldNameLower", StringUtils.lowerFirst(cellInfo.getFieldName()));
            column.put("columnComment", cellInfo.getFieldComment());
            column.put("fieldTypeString", cellInfo.getFieldTypeString());
            column.put("fieldBelong", cellInfo.getFieldBelong());
            columns.add(column);
        }
        parse.put("columns", columns);

        TemplatePack templatePack = TemplatePack.build(this.getClass().getClassLoader(), "excel-code");
        String tmpPath = outPath + packageName.replace(".", "/") + "/";

        {
            File file = new File(tmpPath + "bean/mapping/" + tableData.getCodeClassName() + "Mapping.java");
            templatePack.ftl2File("mapping.ftl", parse, file.getPath());
            log.info("生成 映射 文件：{}, {}, {}", tableData.getTableComment(), tableData.getTableName(), FileUtil.getCanonicalPath(file));
        }

        {
            File file = new File(tmpPath + "bean/" + tableData.getCodeClassName() + ".java");
            templatePack.ftl2File("bean.ftl", parse, file.getPath());
            log.info("生成 bean 文件：{}, {}, {}", tableData.getTableComment(), tableData.getTableName(), FileUtil.getCanonicalPath(file));
        }

        {
            File file = new File(tmpPath + "/" + tableData.getCodeClassName() + "Table.java");
            templatePack.ftl2File("table.ftl", parse, file.getPath());
            log.info("生成 table 文件：{}, {}, {}", tableData.getTableComment(), tableData.getTableName(), FileUtil.getCanonicalPath(file));
        }
    }

}
