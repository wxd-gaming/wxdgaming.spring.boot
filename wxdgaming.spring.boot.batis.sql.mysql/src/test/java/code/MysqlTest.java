package code;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.spring.boot.batis.sql.mysql.MysqlConfiguration;
import wxdgaming.spring.boot.batis.sql.mysql.MysqlDataHelper;
import wxdgaming.spring.boot.core.CoreConfiguration;

@SpringBootTest(
        classes = {
                CoreConfiguration.class,
                MysqlConfiguration.class,
                MysqlTest.class
        }
)
class MysqlTest {

    @Autowired
    private MysqlDataHelper mysqlDataHelper;


    @Test
    void contextLoads() {
        String dbName = mysqlDataHelper.getDbName();
        System.out.println(dbName);
    }

}
