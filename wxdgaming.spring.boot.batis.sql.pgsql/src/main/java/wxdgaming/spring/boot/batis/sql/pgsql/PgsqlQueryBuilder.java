package wxdgaming.spring.boot.batis.sql.pgsql;

import wxdgaming.spring.boot.batis.sql.SqlQueryBuilder;

/**
 * pgsql 构建器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-21 09:50
 **/
public class PgsqlQueryBuilder extends SqlQueryBuilder {

    public PgsqlQueryBuilder(PgsqlDataHelper sqlDataHelper) {
        super(sqlDataHelper);
    }

    @Override public String getTableName() {
        return super.getTableName().replace('`', '"');
    }

    @Override public String buildSelectSql() {
        return super.buildSelectSql().replace('`', '"');
    }
}
