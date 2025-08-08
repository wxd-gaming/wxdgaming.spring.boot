package wxdgaming.spring.boot.batis.sql.pgsql;

import wxdgaming.spring.boot.batis.ColumnType;
import wxdgaming.spring.boot.batis.Entity;
import wxdgaming.spring.boot.batis.TableMapping;
import wxdgaming.spring.boot.batis.sql.SqlDDLBuilder;
import wxdgaming.spring.boot.batis.sql.ann.Partition;
import wxdgaming.spring.boot.core.reflect.AnnUtil;

/**
 * pgsql
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-15 17:51
 **/
public class PgSqlDDLBuilder extends SqlDDLBuilder {

    @Override public StringBuilder buildTableSqlString(TableMapping tableMapping, String tableName) {
        StringBuilder table = super.buildTableSqlString(tableMapping, tableName);

        tableMapping.getColumns().values()
                .stream()
                .filter(v -> AnnUtil.ann(v.getField(), Partition.class) != null)
                .findFirst()
                .ifPresent(fieldMapping -> {
                    table.append(" PARTITION BY RANGE").append("(\"").append(fieldMapping.getColumnName()).append("\")");
                });

        return table;
    }

    @Override public String buildColumnDefinition(TableMapping.FieldMapping fieldMapping) {
        ColumnType columnType = fieldMapping.getColumnType();
        String columnDefinition = null;
        switch (columnType) {
            case Bool -> {
                columnDefinition = "bool";
            }
            case Byte, Short -> {
                columnDefinition = "int2";
            }
            case Int -> {
                columnDefinition = "int4";
            }
            case Long -> {
                columnDefinition = "int8";
            }
            case Float -> {
                columnDefinition = "float4";
            }
            case Double -> {
                columnDefinition = "float8";
            }
            case String -> {
                if (fieldMapping.getLength() < 5000)
                    columnDefinition = "VARCHAR(%s)".formatted(fieldMapping.getLength());
                else
                    columnDefinition = "text";
            }
            case Blob -> {
                columnDefinition = "BYTEA";
            }
            case Json -> {
                columnDefinition = "JSON";
            }
            case Jsonb -> {
                columnDefinition = "JSONB";
            }
            case null, default -> {
                throw new RuntimeException("无法处理的数据库类型 " + columnType);
            }
        }
        if (!fieldMapping.isNullable()) {
            columnDefinition += " NOT NULL";
        }
        return columnDefinition;
    }

    @Override public String buildAlterColumnIndex(String tableName, TableMapping.FieldMapping fieldMapping) {
        String columnName = fieldMapping.getColumnName();
        if (fieldMapping.getColumnType() == ColumnType.Json || fieldMapping.getColumnType() == ColumnType.Jsonb) {
            return "CREATE INDEX \"%s_%s\" ON \"%s\" USING gin(\"%s\");".formatted(tableName, columnName, tableName, columnName);
        } else {
            return "CREATE INDEX \"%s_%s\" ON \"%s\" (\"%s\");".formatted(tableName, columnName, tableName, columnName);
        }
    }

    /** pgsql 如果字段是json的 ? => ?::json */
    @Override public String build$$(TableMapping.FieldMapping fieldMapping) {
        return switch (fieldMapping.getColumnType()) {
            case Json -> "?::json";
            case Jsonb -> "?::jsonb";
            case Byte, Short -> "?::int2";
            case null, default -> super.build$$(fieldMapping);
        };
    }

    /** sql语句中替换`为" */
    @Override public String buildSql$$(String sql) {
        return super.buildSql$$(sql).replace("`", "\"");
    }

    @Override public String buildExitSql(Entity entity) {
        TableMapping tableMapping = tableMapping(entity.getClass());
        String tableName = TableMapping.beanTableName(entity);
        return tableMapping.getExitSql().computeIfAbsent(
                tableName,
                k -> {
                    String sql = "select 1 as \"exits\" from \"" + tableName + "\"";
                    String where = buildKeyWhere(tableMapping);
                    sql += " where " + where;
                    return sql;
                }
        );
    }
}
