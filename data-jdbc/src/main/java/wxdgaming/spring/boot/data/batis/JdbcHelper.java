package wxdgaming.spring.boot.data.batis;

import com.alibaba.fastjson.JSONObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.function.ConsumerE1;
import wxdgaming.spring.boot.data.EntityUID;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Stream;

/**
 * 数据源配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-02 21:19
 **/
@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties("spring")
@ConditionalOnProperty("spring.db.url")
public class JdbcHelper implements InitPrint {

    DruidSourceConfig db;

    @Bean
    @Primary
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource datasource() {
        db.createDatabase();
        return db.toDataSource();
    }

    public <T> void delete(EntityManager em, Class<T> clazz, Object uid) {
        T t = find(em, clazz, uid);
        em.remove(t);
    }

    public <T> void delete(EntityManager em, T t) {
        em.remove(t);
    }

    public <T> T find(EntityManager em, Class<T> clazz, Object uid) {
        return em.find(clazz, uid);
    }

    public <T> List<T> findAll(EntityManager em, Class<T> clazz) {
        return em.createQuery("from " + clazz.getSimpleName(), clazz).getResultList();
    }

    public <T> Stream<T> findAll2Stream(EntityManager em, Class<T> clazz) {
        return em.createQuery("from " + clazz.getSimpleName(), clazz).getResultStream();
    }

    public <T> Stream<T> findAll2Stream(EntityManager em, String qlString, Class<T> clazz, Object... params) {
        TypedQuery<T> query = em.createQuery(qlString, clazz);
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        return query.getResultStream();
    }

    /** 批量保存数据 ，会先查询数据库是否存在数据，然后决定是插入还是修改，性能较差 */
    public <T extends EntityUID> void batchSave(EntityManager em, List<T> entities) {
        em.getTransaction().begin();
        try {
            for (T entity : entities) {
                Object uid = entity.getUid();
                if (uid != null && !uid.equals(0)) {
                    if (em.find(entity.getClass(), uid) != null) {
                        em.merge(entity);
                        continue;
                    }
                }
                em.persist(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            // 如果发生异常，回滚事务
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        }
    }

    /** 批量插入数据 */
    public <T extends EntityUID> void batchInsert(EntityManager em, List<T> entities) {
        em.getTransaction().begin();
        try {
            for (T entity : entities) {
                em.persist(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            // 如果发生异常，回滚事务
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        }
    }


    /** 批量修改数据 */
    public <T extends EntityUID> void batchUpdate(EntityManager em, List<T> entities) {
        em.getTransaction().begin();
        try {
            for (T entity : entities) {
                em.merge(entity);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            // 如果发生异常，回滚事务
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        }
    }

    public void queryJsonObject(DataSource dataSource, String query, Object[] params, ConsumerE1<JSONObject> consumer) throws Exception {
        query0(dataSource, query, params, resultSet -> {
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int j = 1; j < columnCount + 1; j++) {
                JSONObject jsonObject = new JSONObject();
                Object object = resultSet.getObject(j);
                String columnName = resultSet.getMetaData().getColumnLabel(j);
                jsonObject.put(columnName, object);
                consumer.accept(jsonObject);
            }
        });
    }

    public void query0(DataSource dataSource, String query, Object[] params, ConsumerE1<ResultSet> consumer) throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    consumer.accept(resultSet);
                }
            }
        }
    }

}
