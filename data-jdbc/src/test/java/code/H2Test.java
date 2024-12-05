package code;

import jakarta.persistence.EntityManager;
import org.junit.Test;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.boot.data.MyTestEntity;
import wxdgaming.spring.boot.data.batis.DruidSourceConfig;
import wxdgaming.spring.boot.data.batis.JdbcHelper;

import java.util.ArrayList;
import java.util.Map;

public class H2Test {

    EntityManager entityManager;
    JdbcHelper jdbcHelper = new JdbcHelper();
    HexId hexId = new HexId(1);

    void createFileDriver(String url) {
        DruidSourceConfig db = new DruidSourceConfig();
        db.setUrl(url);
        db.setUsername("sa");
        db.setPassword("");
        db.setDriverClassName("org.h2.Driver");
        db.setPackageNames(new String[]{MyTestEntity.class.getPackageName()});
        db.setBatchInsert(true);
        db.setDialect(org.hibernate.dialect.H2Dialect.class.getName());

        jdbcHelper.setDb(db);
        entityManager = jdbcHelper.getDb().entityManagerFactory(Map.of());
    }

    @Test
    public void insert2File() {
        createFileDriver("jdbc:h2:file:./target/test");
        insert();
    }

    @Test
    public void insert2Mem() {
        createFileDriver("jdbc:h2:mem:test");
        insert();
    }


    public void insert() {
        {
            entityManager.getTransaction().begin();
            entityManager.createNativeQuery("TRUNCATE TABLE MY_TEST_ENTITY;").executeUpdate();
            entityManager.getTransaction().commit();
        }
        for (int j = 0; j < 10; j++) {
            ArrayList<MyTestEntity> logs = new ArrayList<>(10000);
            for (int i = 0; i < 10000; i++) {
                MyTestEntity slog = new MyTestEntity();
                slog.setUid(hexId.newId());
                slog.setName(String.valueOf(i));
                logs.add(slog);
            }
            long start = System.nanoTime();
            jdbcHelper.batchSave(entityManager, logs);
            System.out.println(((System.nanoTime() - start) / 10000 / 100f) + " ms");
        }
    }

}
