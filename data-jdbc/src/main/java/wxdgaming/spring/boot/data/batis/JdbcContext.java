package wxdgaming.spring.boot.data.batis;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import lombok.Getter;
import wxdgaming.spring.boot.core.function.ConsumerE1;
import wxdgaming.spring.boot.data.EntityUID;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.stream.Stream;

/**
 * 上下文容器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-09 14:27
 **/
@Getter
public class JdbcContext {

    private final DruidDataSource dataSource;
    private final EntityManager em;
    private final ThreadLocal<EntityManager> threadContext = new ThreadLocal<>();

    public JdbcContext(DruidDataSource dataSource, EntityManager em) {
        this.dataSource = dataSource;
        this.em = em;
    }

    public EntityManager context() {
        EntityManager entityManager = threadContext.get();
        if (entityManager == null) {
            entityManager = em.getEntityManagerFactory().createEntityManager();
            threadContext.set(entityManager);
        }
        return entityManager;
    }

    public void release(EntityManager entityManager) {
        threadContext.remove();
        entityManager.close();
    }

    protected void releaseVirtual(EntityManager entityManager) {
        if (Thread.currentThread().isVirtual()) {
            release(entityManager);
        }
    }

    /** 执行语句 */
    public int executeUpdate(String updateSql, Object... params) {
        EntityManager entityManager = context();
        entityManager.getTransaction().begin();
        try {
            Query nativeQuery = entityManager.createNativeQuery(updateSql);
            for (int i = 0; i < params.length; i++) {
                nativeQuery.setParameter(i + 1, params[i]);
            }
            int executeUpdate = nativeQuery.executeUpdate();
            entityManager.getTransaction().commit();
            return executeUpdate;
        } catch (Exception e) {
            // 如果发生异常，回滚事务
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            releaseVirtual(entityManager);
        }
    }

    public <T extends EntityUID> long count(Class<T> t) {
        String qlString = "select count(1) from " + t.getSimpleName();
        return count(qlString);
    }

    public long count(String qlString, Object... params) {
        TypedQuery<Long> query = context().createQuery(qlString, Long.class);
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        return query.getSingleResult();
    }

    public <T extends EntityUID> void delete(Class<T> clazz, Object uid) {
        T t = find(clazz, uid);
        delete(t);
    }

    // public Integer delete0(Class<?> clazz, Object uid) {
    //     EntityManager entityManager = context();
    //     entityManager.getTransaction().begin();
    //     try {
    //         Query nativeQuery = entityManager.createNativeQuery("delete from " + clazz.getSimpleName() + " t where t.uid=?1", Integer.class);
    //         nativeQuery.setParameter(1, uid);
    //         Integer singleResult = nativeQuery.executeUpdate();
    //         entityManager.getTransaction().commit();
    //         return singleResult;
    //     } catch (Exception e) {
    //         // 如果发生异常，回滚事务
    //         if (entityManager.getTransaction().isActive()) {
    //             entityManager.getTransaction().rollback();
    //         }
    //         throw new RuntimeException(e);
    //     } finally {
    //         releaseVirtual(entityManager);
    //     }
    // }

    /**
     * 删除一条数据
     *
     * @param t   数据
     * @param <T> 实体类
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-12-25 13:54
     */
    public <T extends EntityUID> void delete(T t) {
        EntityManager entityManager = context();
        entityManager.getTransaction().begin();
        try {
            T merge = entityManager.merge(t);
            entityManager.remove(merge);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            // 如果发生异常，回滚事务
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            releaseVirtual(entityManager);
        }
    }

    public <T extends EntityUID> T find(Class<T> clazz, Object uid) {
        EntityManager entityManager = context();
        try {
            return entityManager.find(clazz, uid);
        } finally {
            releaseVirtual(entityManager);
        }
    }

    public <T extends EntityUID> List<T> findAll(Class<T> clazz) {
        EntityManager entityManager = context();
        try {
            return entityManager.createQuery("from " + clazz.getSimpleName(), clazz).getResultList();
        } finally {
            releaseVirtual(entityManager);
        }
    }

    public <T extends EntityUID> Stream<T> findAll2Stream(Class<T> clazz) {
        EntityManager entityManager = context();
        try {
            return entityManager.createQuery("from " + clazz.getSimpleName(), clazz).getResultStream();
        } finally {
            releaseVirtual(entityManager);
        }
    }

    public <T extends EntityUID> Stream<T> findAll2Stream(String qlString, Class<T> clazz, Object... params) {
        TypedQuery<T> query = context().createQuery(qlString, clazz);
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        return query.getResultStream();
    }

    /** 批量保存数据 ，会先查询数据库是否存在数据，然后决定是插入还是修改，性能较差 */
    public <T extends EntityUID> void save(T entity) {
        EntityManager entityManager = context();
        entityManager.getTransaction().begin();
        try {
            boolean isNew = true;
            Object uid = entity.getUid();
            if (uid != null && !uid.equals(0)) {
                if (entityManager.find(entity.getClass(), uid) != null) {
                    entityManager.merge(entity);
                    isNew = false;
                }
            }
            if (isNew) {
                entityManager.persist(entity);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            // 如果发生异常，回滚事务
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            releaseVirtual(entityManager);
        }
    }

    /** 批量保存数据 ，会先查询数据库是否存在数据，然后决定是插入还是修改，性能较差 */
    public <T extends EntityUID> void batchSave(List<T> entities) {
        EntityManager entityManager = context();
        entityManager.getTransaction().begin();
        try {
            for (T entity : entities) {
                Object uid = entity.getUid();
                if (uid != null && !uid.equals(0)) {
                    if (entityManager.find(entity.getClass(), uid) != null) {
                        entityManager.merge(entity);
                        continue;
                    }
                }
                entityManager.persist(entity);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            // 如果发生异常，回滚事务
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            releaseVirtual(entityManager);
        }
    }


    /** 批量插入数据 */
    public <T extends EntityUID> void batchInsert(List<T> entities) {
        EntityManager entityManager = context();
        entityManager.getTransaction().begin();
        try {
            for (T entity : entities) {
                entityManager.persist(entity);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            // 如果发生异常，回滚事务
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            releaseVirtual(entityManager);
        }
    }


    /** 批量修改数据 */
    public <T extends EntityUID> void batchUpdate(List<T> entities) {
        EntityManager entityManager = context();
        entityManager.getTransaction().begin();
        try {
            for (T entity : entities) {
                entityManager.merge(entity);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            // 如果发生异常，回滚事务
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            releaseVirtual(entityManager);
        }
    }

    public <T extends EntityUID> void batchDelete(List<T> entities) {
        EntityManager entityManager = context();
        entityManager.getTransaction().begin();
        try {
            for (T entity : entities) {
                T merge = entityManager.merge(entity);
                if (merge == null) {
                    continue;
                }
                entityManager.remove(merge);
            }
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            // 如果发生异常，回滚事务
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            throw new RuntimeException(e);
        } finally {
            releaseVirtual(entityManager);
        }
    }

    public void queryJsonObject(String query, Object[] params, ConsumerE1<JSONObject> consumer) throws Exception {
        query0(query, params, resultSet -> {
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

    public void query0(String query, Object[] params, ConsumerE1<ResultSet> consumer) throws Exception {
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
