package code;

import jakarta.persistence.EntityManager;
import org.junit.Before;
import org.junit.Test;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.boot.data.MyTestEntity;
import wxdgaming.spring.boot.data.batis.DruidSourceConfig;
import wxdgaming.spring.boot.data.batis.JdbcHelper;

import java.util.ArrayList;
import java.util.List;
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

    @Before
    public void insert2File() {
        createFileDriver("jdbc:h2:file:./target/test");
    }

    @Test
    public void insert2Mem() {
        createFileDriver("jdbc:h2:mem:test");
    }


    @Test
    public void clearTable() {
        {
            entityManager.getTransaction().begin();
            entityManager.createNativeQuery("delete from MY_TEST_ENTITY").executeUpdate();
            entityManager.getTransaction().commit();
        }
    }

    @Test
    public void insert() {
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

    @Test
    public void insertMerge() {
        for (int k = 0; k < 2; k++) {

            ArrayList<MyTestEntity> logs = new ArrayList<>(10);
            for (int i = 0; i < 10; i++) {
                MyTestEntity slog = new MyTestEntity();
                slog.setUid(i + 1L);
                logs.add(slog);
            }
            long start = System.nanoTime();
            jdbcHelper.batchSave(entityManager, logs);
            System.out.println(((System.nanoTime() - start) / 10000 / 100f) + " ms");
        }
        List<MyTestEntity> all = jdbcHelper.findAll(entityManager, MyTestEntity.class);
        for (MyTestEntity myTestEntity : all) {
            System.out.println(myTestEntity.toJson());
        }
    }

    @Test
    public void select() {
        long count = jdbcHelper.findAll2Stream(entityManager, "from MyTestEntity as m where m.uid > ?1", MyTestEntity.class, 1L)
                .peek(myTestEntity -> System.out.println(myTestEntity.toJson()))
                .count();
        System.out.println(count);
    }


}
