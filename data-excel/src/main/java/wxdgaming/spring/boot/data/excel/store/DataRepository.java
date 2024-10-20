package wxdgaming.spring.boot.data.excel.store;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.ReflectContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据仓库
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-09 11:35
 **/
@Slf4j
@Getter
@Accessors(chain = true)
@Service
public class DataRepository {

    @Setter private String jsonPath;
    @Setter private ClassLoader classLoader;
    @Setter private String scanPackageName;
    /** 存储数据表 */
    private Map<Class<?>, DataTable<?>> dataTableMap = Map.of();

    public <D extends DataKey, T extends DataTable<D>> T dataTable(Class<T> dataTableClass) {
        return (T) dataTableMap.get(dataTableClass);
    }

    public void load() {
        Map<Class<?>, DataTable<?>> tmpDataTableMap = new ConcurrentHashMap<>();
        ReflectContext reflectContext = ReflectContext.Builder.of(classLoader, scanPackageName).build();
        reflectContext.classWithSuper(DataTable.class, null)
                .forEach(dataTableClass -> {
                    try {
                        DataTable<?> dataTable = buildDataTable(dataTableClass);
                        tmpDataTableMap.put(dataTableClass, dataTable);
                    } catch (Exception e) {
                        log.error("load data table error", e);
                    }
                });
        for (DataTable<?> dataTable : tmpDataTableMap.values()) {
            dataTable.checkData(tmpDataTableMap);
        }
        dataTableMap = tmpDataTableMap;
    }

    public void reload(Class<?> dataTableClass) {
        dataTableMap.get(dataTableClass).loadJson(jsonPath);
    }

    DataTable<?> buildDataTable(Class<?> dataTableClass) throws Exception {
        DataTable<?> dataTable = (DataTable<?>) dataTableClass.getDeclaredConstructor().newInstance();
        dataTable.loadJson(jsonPath);
        log.info("load data table 文件：{}, 数据：{}, 行数：{}", dataTable.getDataMapping().excelPath(), dataTable.getDataMapping().name(), dataTable.dbSize());
        return dataTable;
    }

}
