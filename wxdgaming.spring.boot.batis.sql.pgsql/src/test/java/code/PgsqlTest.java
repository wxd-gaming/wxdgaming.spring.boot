package code;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlConfiguration;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlDataHelper;
import wxdgaming.spring.boot.core.CoreConfiguration;

@SpringBootTest(
        classes = {
                CoreConfiguration.class,
                PgsqlConfiguration.class
        }
)
class PgsqlTest {

    @Autowired
    private PgsqlDataHelper pgsqlDataHelper;


    @Test
    void contextLoads() {
        String dbName = pgsqlDataHelper.getDbName();
        System.out.println(dbName);
    }
}
