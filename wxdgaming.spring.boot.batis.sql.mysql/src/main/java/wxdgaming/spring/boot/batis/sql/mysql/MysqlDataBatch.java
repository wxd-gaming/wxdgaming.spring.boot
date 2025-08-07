package wxdgaming.spring.boot.batis.sql.mysql;


import wxdgaming.spring.boot.batis.sql.SqlDataBatch;

/**
 * sql 模型 批量
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-16 20:58
 **/
public class MysqlDataBatch extends SqlDataBatch {

    public MysqlDataBatch(MysqlDataHelper dataHelper) {
        super(dataHelper);
    }

    @Override public MysqlDataHelper dataHelper() {
        return super.dataHelper();
    }

}
