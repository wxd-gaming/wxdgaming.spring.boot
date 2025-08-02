package wxdgaming.spring.boot.batis.sql.mysql;

import wxdgaming.spring.boot.batis.TableMapping;
import wxdgaming.spring.boot.batis.sql.SqlDDLBuilder;
import wxdgaming.spring.boot.batis.sql.ann.Partition;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.reflect.AnnUtil;

/**
 * pgsql
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-15 17:51
 **/
public class MySqlDDLBuilder extends SqlDDLBuilder {

    @Override public StringBuilder buildTableSqlString(TableMapping tableMapping, String tableName) {
        StringBuilder tableSql = super.buildTableSqlString(tableMapping, tableName);
        tableMapping.getColumns().values()
                .stream()
                .filter(v -> AnnUtil.ann(v.getField(), Partition.class) != null)
                .findFirst()
                .ifPresent(fieldMapping -> {
                    Partition partition = AnnUtil.ann(fieldMapping.getField(), Partition.class);
                    String minRangeValue = partition.mysqlInitMinRangeValue();
                    String[] strings = partition.initRangeArrays();
                    tableSql.append(" PARTITION BY RANGE")
                            .append("(").append(fieldMapping.getColumnName()).append(")");
                    if (strings != null && strings.length > 0 && StringUtils.isNotBlank(strings[0])) {
                        tableSql.append("(");
                        for (int i = 0; i < strings.length; i++) {
                            String s = strings[i];
                            if (StringUtils.isBlank(s)) {
                                continue;
                            }
                            tableSql.append("PARTITION p_%s VALUES LESS THAN (%s)".formatted(i, s));
                        }
                        tableSql.append(")");
                    } else {
                        tableSql.append("(PARTITION p_min_value VALUES LESS THAN (%s))".formatted(minRangeValue));
                    }
                });
        return tableSql;
    }

}
