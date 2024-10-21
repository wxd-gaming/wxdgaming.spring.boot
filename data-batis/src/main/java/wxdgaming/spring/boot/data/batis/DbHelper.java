package wxdgaming.spring.boot.data.batis;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import jakarta.annotation.PostConstruct;
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
import wxdgaming.spring.boot.core.Throw;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.function.Consumer;
import java.util.regex.Pattern;

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
@ConfigurationProperties("spring.datasource")
@ConditionalOnProperty("spring.datasource.url")
public class DbHelper implements InitPrint {

    private final Pattern DB_NAME_REGEX = Pattern.compile("[^/]+(?=(\\?|$))");

    String url;
    String username;
    String password;

    DruidDataSource dataSource;

    @PostConstruct
    public void initDb() {
        String dbName = getDbName(url);
        createDatabase(dbName);

    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(DataSource.class)
    public DataSource datasource() {
        dataSource = DruidDataSourceBuilder.create().build();
        log.info("初始化DataSource：{}", url);
        return dataSource;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * 获取指定数据库的链接
     *
     * @param dbnameString 数据库名字
     * @return
     */
    public Connection getConnection(String dbnameString) {
        try {
            return DriverManager.getConnection(
                    url.replace(getDbName(url), dbnameString),
                    username,
                    password
            );
        } catch (SQLException e) {
            throw Throw.of(e);
        }
    }

    public String getDbName(String connectString) {
        int indexOf = connectString.indexOf("?");
        if (indexOf > 0) {
            connectString = connectString.substring(0, indexOf);
        }
        int indexOf1 = connectString.lastIndexOf('/');
        connectString = connectString.substring(indexOf1 + 1);
        return connectString;
    }

    public String createDatabaseString(String database, String daoCharacter) {
        return "CREATE DATABASE IF NOT EXISTS `" + database.toLowerCase() + "` DEFAULT CHARACTER SET " + daoCharacter + " COLLATE " + daoCharacter + "_unicode_ci;";
    }

    /** 创建数据库 , 吃方法创建数据库后会自动使用 use 语句 */
    public void createDatabase(String database) {
        try (Connection connection = getConnection("INFORMATION_SCHEMA")) {
            Consumer<String> stringConsumer = (character) -> {
                String databaseString = createDatabaseString(database, character);
                try (Statement statement = connection.createStatement()) {
                    int update = statement.executeUpdate(databaseString);
                    log.info("数据库 {} 创建 {}", database, update);
                } catch (Exception e) {
                    throw Throw.of(e);
                }
            };
            try {
                stringConsumer.accept("utf8mb4");
            } catch (Throwable t) {
                if (t.getMessage().contains("utf8mb4")) {
                    log.warn("数据库 {} 不支持 utf8mb4 格式 重新用 utf8 字符集创建数据库", database, new RuntimeException());
                    stringConsumer.accept("utf8");
                } else {
                    log.error("创建数据库 {}", database, t);
                }
            }
        } catch (Exception e) {
            log.error("创建数据库 {}", database, e);
        }
    }

}
