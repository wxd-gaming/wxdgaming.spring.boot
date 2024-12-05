package code;

import jakarta.persistence.EntityManager;
import org.junit.Test;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.boot.data.MyTestEntity;
import wxdgaming.spring.boot.data.batis.DruidSourceConfig;
import wxdgaming.spring.boot.data.batis.JdbcHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class H2Test {

    EntityManager entityManager;
    JdbcHelper jdbcHelper = new JdbcHelper();
    HexId hexId = new HexId(1);

    public H2Test() {
        jdbcHelper.setDb(new DruidSourceConfig());
        jdbcHelper.getDb().setUrl("jdbc:h2:file:./target/test");
        jdbcHelper.getDb().setUsername("sa");
        jdbcHelper.getDb().setPassword("");
        jdbcHelper.getDb().setDriverClassName("org.h2.Driver");
        jdbcHelper.getDb().setPackageNames(new String[]{MyTestEntity.class.getPackageName()});
        Map<String, Object> jpaConfig = new HashMap<>();
        jpaConfig.put("database-platform", org.hibernate.dialect.H2Dialect.class.getName());
        entityManager = jdbcHelper.getDb().entityManagerFactory(jpaConfig);
    }

    @Test
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
