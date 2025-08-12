package query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.spring.boot.batis.sql.SqlQueryBuilder;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlConfiguration;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.logserver.bean.LogEntity;

import java.util.List;

@SpringBootTest(
        classes = {
                CoreConfiguration.class,
                PgsqlConfiguration.class,
        }
)
public class LogQueryTest {

    @Autowired PgsqlDataHelper pgsqlDataHelper;

    @Test
    public void l1() {
        SqlQueryBuilder sqlQueryBuilder = pgsqlDataHelper.queryBuilder();
        sqlQueryBuilder.sqlByEntity(LogEntity.class)
                .setTableName("login")
                .pushWhere("""
                        logdata::jsonb @> jsonb_build_object('account',?)""", "kQj2JQ9N")
                .pushWhere("""
                        logdata::jsonb @> jsonb_build_object('account',?)""", "kQj2JQ9N");
        System.out.println(sqlQueryBuilder.buildSelectSql());
        List<LogEntity> x = sqlQueryBuilder.findList2Entity(LogEntity.class);
        for (LogEntity logEntity : x) {
            System.out.println(logEntity.toJSONString());
        }
    }

    @Test
    public void sumRecharge() {
        SqlQueryBuilder sqlQueryBuilder = pgsqlDataHelper.queryBuilder();
        sqlQueryBuilder
                .setSelectField("sum((logdata::jsonb ->> 'money')::numeric) as money")
                .setTableName("recharge")
                .pushWhere("""
                        logdata::jsonb @> jsonb_build_object('account',?)""", "Zkk9zRv9");
        System.out.println(sqlQueryBuilder.buildSelectSql());
        Long executeScalar = sqlQueryBuilder.executeScalar(Long.class);
        System.out.println(executeScalar);
    }

}
