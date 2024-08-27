package wxdgaming.spring.boot.data.batis;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
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
public class DataSourceHelper implements InitPrint {

    private final Pattern DB_NAME_REGEX = Pattern.compile("[^\\/]+(?=(\\?|$))");
    /**
     * 默认utf8
     */
    public static String DAOCHARACTER = "utf8mb4";

    String url;
    String username;
    String password;

    DruidDataSource dataSource;

    @PostConstruct
    public void initDb() {
        String dbName = getDbName(url);
        boolean database = createDatabase(dbName);
        log.info("数据库 {} 创建 {}", dbName, database);
    }

    @Bean
    @Primary
    public DataSource datasource() {
        dataSource = DruidDataSourceBuilder.create().build();
        log.info("初始化DataSource：{}", url);
        return dataSource;
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * @param dbnameString
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

    /** 创建数据库 , 吃方法创建数据库后会自动使用 use 语句 */
    public boolean createDatabase(String database) {
        try (Connection connection = getConnection("INFORMATION_SCHEMA")) {
            StringBuilder stringBuilder = new StringBuilder();
            try {
                stringBuilder.append("CREATE DATABASE IF NOT EXISTS `")
                        .append(database.toLowerCase())
                        .append("` DEFAULT CHARACTER SET ")
                        .append(DAOCHARACTER)
                        .append(" COLLATE ")
                        .append(DAOCHARACTER)
                        .append("_unicode_ci;");
                return connection.createStatement().execute(stringBuilder.toString());
            } catch (Exception e) {
                if (e.getMessage().contains("utf8mb4")) {
                    DAOCHARACTER = "utf8";
                    log.warn("数据库 " + database + " 不支持 utf8mb4 格式 重新用 utf8 字符集创建数据库", new RuntimeException());
                    stringBuilder.setLength(0);
                    stringBuilder.append("CREATE DATABASE IF NOT EXISTS `")
                            .append(database.toLowerCase())
                            .append("` DEFAULT CHARACTER SET ")
                            .append(DAOCHARACTER)
                            .append(" COLLATE ")
                            .append(DAOCHARACTER)
                            .append("_unicode_ci;");
                    return connection.createStatement().execute(stringBuilder.toString());
                } else {
                    log.error("创建数据库 {}", database, e);
                }
            }
        } catch (Exception e) {
            log.error("创建数据库 {}", database, e);
        }
        return false;
    }

}
