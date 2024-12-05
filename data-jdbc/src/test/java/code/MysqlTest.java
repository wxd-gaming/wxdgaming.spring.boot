package code;

import jakarta.persistence.EntityManager;
import org.junit.Test;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.boot.data.MyTestEntity;
import wxdgaming.spring.boot.data.batis.DruidSourceConfig;
import wxdgaming.spring.boot.data.batis.JdbcHelper;

import java.util.ArrayList;
import java.util.Map;

public class MysqlTest {

    EntityManager entityManager;
    JdbcHelper jdbcHelper = new JdbcHelper();
    HexId hexId = new HexId(1);

    public MysqlTest() {
        DruidSourceConfig db = new DruidSourceConfig();
        db.setUrl("jdbc:mysql://localhost:3306/test?serverTimezone=UTC&autoReconnect=true&useUnicode=true&characterEncoding=utf8&useSSL=true&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true&rewriteBatchedStatements=true");
        db.setDriverClassName("com.mysql.cj.jdbc.Driver");
        db.setUsername("root");
        db.setPassword("test");
        db.setShowSql(false);
        db.setDdlAuto("update");
        db.setDialect(org.hibernate.dialect.MySQLDialect.class.getName());
        db.createDatabase();

        jdbcHelper.setDb(db);
        jdbcHelper.getDb().setPackageNames(new String[]{MyTestEntity.class.getPackageName()});
        entityManager = jdbcHelper.getDb().entityManagerFactory(Map.of());
    }

    @Test
    public void insert() {
        {
            entityManager.getTransaction().begin();
            entityManager.createNativeQuery("TRUNCATE TABLE my_test_entity;").executeUpdate();
            entityManager.getTransaction().commit();
        }
        for (int j = 0; j < 10; j++) {
            ArrayList<MyTestEntity> logs = new ArrayList<>(10000);
            for (int i = 0; i < 10000; i++) {
                MyTestEntity slog = new MyTestEntity();
                slog.setUid(hexId.newId());
                logs.add(slog);
            }
            long start = System.nanoTime();
            jdbcHelper.batchSave(entityManager, logs);
            System.out.println(((System.nanoTime() - start) / 10000 / 100f) + " ms");
        }
    }

}
