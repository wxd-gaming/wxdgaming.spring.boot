package wxdgaming.spring.boot.batis.sql.mysql;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.batis.sql.SqlConfig;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.InitPrint;

/**
 * 扫描器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-18 08:34
 **/

@Getter
@Setter
@ComponentScan(basePackageClasses = {CoreConfiguration.class})
@Configuration
@ConfigurationProperties(prefix = "db.sql")
@EnableConfigurationProperties
public class MysqlConfiguration implements InitPrint {

    private SqlConfig mysql;
    private SqlConfig mysqlSecond;

    @Autowired
    public MysqlConfiguration(CoreConfiguration coreConfiguration) {}

    @PostConstruct
    public void init() {
        System.out.println(1);
    }

    @Bean
    @ConditionalOnProperty(name = "db.sql.mysql.url")
    public MysqlDataHelper mysqlDataHelper() {
        return new MysqlDataHelper(mysql);
    }

    @Bean
    @ConditionalOnProperty(name = "db.sql.mysql-second.url")
    public MysqlDataHelper mysqlSecond() {
        return new MysqlDataHelper(mysql);
    }

}
