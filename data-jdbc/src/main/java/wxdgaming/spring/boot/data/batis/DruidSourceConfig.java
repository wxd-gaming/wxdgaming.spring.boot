package wxdgaming.spring.boot.data.batis;

import com.alibaba.druid.pool.DruidDataSource;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import wxdgaming.spring.boot.core.Throw;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.lang.ObjectBase;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 连接池配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-11-26 19:42
 **/
@Slf4j
@Getter
@Setter
public class DruidSourceConfig extends ObjectBase {

    /** 需要提高批量模式增加参数 &allowMultiQueries=true&rewriteBatchedStatements=true */
    String url;
    String username;
    String password;
    String driverClassName;
    String[] packageNames;
    int initialSize = 5;
    int minIdle = 5;
    int maxActive = 20;
    int maxWait = 20000;
    boolean testOnBorrow = false;
    boolean testOnReturn = false;
    boolean testWhileIdle = true;
    String validationQuery = "SELECT 1";
    int timeBetweenEvictionRunsMillis = 60000;
    int minEvictableIdleTimeMillis = 300000;
    int maxEvictableIdleTimeMillis = 300000;
    boolean logAbandoned = false;
    boolean poolPreparedStatements = true;
    int maxPoolPreparedStatementPerConnectionSize = 20;
    boolean showSql = false;
    String ddlAuto = "update";
    String dbName = null;
    boolean batchInsert = true;
    boolean batchUpdate = true;
    int batchSize = 200;

    /**
     * 复制一个新的连接配置
     *
     * @param dbName 新的数据库
     * @return DruidSourceConfig
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-11-26 20:27
     */
    public DruidSourceConfig copy(String dbName) {
        String json = FastJsonUtil.toJson(this);
        DruidSourceConfig parse = FastJsonUtil.parse(json, DruidSourceConfig.class);
        parse.setDbName(dbName);
        parse.url = this.url.replace(this.getDbName(), dbName);
        parse.createDatabase();
        return parse;
    }

    public DruidDataSource toDataSource() {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setInitialSize(initialSize);
        dataSource.setMinIdle(minIdle);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxWait(maxWait);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        dataSource.setMaxEvictableIdleTimeMillis(maxEvictableIdleTimeMillis);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestWhileIdle(testWhileIdle);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setTestOnReturn(testOnReturn);
        dataSource.setPoolPreparedStatements(poolPreparedStatements);
        dataSource.setLogAbandoned(logAbandoned);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
        return dataSource;
    }

    @Transactional
    public EntityManager entityManagerFactory(Map<String, Object> jpaConfig) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(toDataSource());
        em.setPackagesToScan(packageNames); // 替换为你的实体包路径
        // 设置命名策略
        em.getJpaPropertyMap().put("hibernate.show_sql", showSql);
        em.getJpaPropertyMap().put("hibernate.hbm2ddl.auto", ddlAuto);
        em.getJpaPropertyMap().put("hibernate.order_inserts", batchInsert);
        em.getJpaPropertyMap().put("hibernate.order_updates", batchUpdate);
        em.getJpaPropertyMap().put("hibernate.jdbc.batch_size", batchSize);
        em.getJpaPropertyMap().putAll(jpaConfig);
        em.getJpaPropertyMap().put("hibernate.physical_naming_strategy", CamelCaseToUnderscoresNamingStrategy.class.getName());
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.afterPropertiesSet(); // 初始化 EntityManagerFactory
        return em.createNativeEntityManager(Map.of());
    }


    public String getDbName() {
        if (StringsUtil.emptyOrNull(dbName)) {
            dbName = url;
            int indexOf = dbName.indexOf("?");
            if (indexOf > 0) {
                dbName = dbName.substring(0, indexOf);
            }
            int indexOf1 = dbName.lastIndexOf('/');
            dbName = dbName.substring(indexOf1 + 1);
        }
        return dbName;
    }

    private String createDatabaseString(String database, String daoCharacter) {
        return "CREATE DATABASE IF NOT EXISTS `%s` DEFAULT CHARACTER SET %s COLLATE %s_unicode_ci;"
                .formatted(database.toLowerCase(), daoCharacter, daoCharacter);
    }

    /** 创建数据库 , 吃方法创建数据库后会自动使用 use 语句 */
    public void createDatabase() {
        String dbName = getDbName();
        try (Connection connection = getConnection()) {
            Consumer<String> stringConsumer = (character) -> {
                String databaseString = createDatabaseString(dbName, character);
                try (Statement statement = connection.createStatement()) {
                    int update = statement.executeUpdate(databaseString);
                    log.info("数据库 {} 创建 {}", dbName, update);
                } catch (Exception e) {
                    throw Throw.of(e);
                }
            };
            try {
                stringConsumer.accept("utf8mb4");
            } catch (Throwable t) {
                if (t.getMessage().contains("utf8mb4")) {
                    log.warn("数据库 {} 不支持 utf8mb4 格式 重新用 utf8 字符集创建数据库", dbName, new RuntimeException());
                    stringConsumer.accept("utf8");
                } else {
                    log.error("创建数据库 {}", dbName, t);
                }
            }
        } catch (Exception e) {
            log.error("创建数据库 {}", dbName, e);
        }
    }

    private Connection getConnection() {
        try {
            Class.forName(getDriverClassName());
            return DriverManager.getConnection(
                    url.replace(getDbName(), "INFORMATION_SCHEMA"),
                    username,
                    password
            );
        } catch (Exception e) {
            throw Throw.of(e);
        }
    }

}
