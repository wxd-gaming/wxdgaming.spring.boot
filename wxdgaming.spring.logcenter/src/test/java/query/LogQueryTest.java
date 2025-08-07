package query;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.spring.boot.batis.sql.SqlQueryBuilder;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlConfiguration;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.logcenter.bean.LogEntity;

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
                .pushWhere("createtime >= ?", 1754569211194L)
                .pushWhere("createtime <= ?", 1754569211194L)
                .pushWhere("json_extract_path_text(json,'account') = ?", "wxd-gaming");
        System.out.println(sqlQueryBuilder.buildSelectSql());
        List<LogEntity> x = sqlQueryBuilder.findList2Entity(LogEntity.class);
        for (LogEntity logEntity : x) {
            System.out.println(logEntity.toJSONString());
        }
    }

}
