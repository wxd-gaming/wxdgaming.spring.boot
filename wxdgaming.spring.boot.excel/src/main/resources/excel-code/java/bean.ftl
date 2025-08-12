package ${packageName}.bean;


import lombok.Getter;
import wxdgaming.spring.boot.excel.store.DataChecked;
import wxdgaming.spring.boot.excel.store.DataTable;
import ${packageName}.bean.mapping.${codeClassName}Mapping;

import java.io.Serializable;
import java.util.Map;


/**
 * excel 构建 ${tableComment}, ${filePath}, ${tableName},
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: ${.now?string("yyyy-MM-dd HH:mm:ss")}
 **/
@Getter
public class ${codeClassName} extends ${codeClassName}Mapping implements Serializable, DataChecked {

    @Override public void initAndCheck(Map<Class<?>, DataTable<?>> store) throws Exception {
        /*todo 实现数据检测和初始化*/

    }

}
