package wxdgaming.spring.boot.batis.sql.mysql;


import wxdgaming.spring.boot.batis.sql.SqlQueryBuilder;

/**
 * pgsql 构建器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-21 09:50
 **/
public class MysqlQueryBuilder extends SqlQueryBuilder {

    public MysqlQueryBuilder(MysqlDataHelper sqlDataHelper) {
        super(sqlDataHelper);
    }

}
