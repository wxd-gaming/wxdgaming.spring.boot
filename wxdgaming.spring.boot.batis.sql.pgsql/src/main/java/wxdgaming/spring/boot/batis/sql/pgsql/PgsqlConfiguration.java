package wxdgaming.spring.boot.batis.sql.pgsql;

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
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-18 08:34
 **/
@Getter
@Setter
@ComponentScan(basePackageClasses = {CoreConfiguration.class})
@Configuration
@ConfigurationProperties(prefix = "db.sql")
@EnableConfigurationProperties
public class PgsqlConfiguration implements InitPrint {

    private SqlConfig pgsql;
    private SqlConfig pgsqlSecond;

    @Autowired
    public PgsqlConfiguration(CoreConfiguration coreConfiguration) {}

    @Bean
    @ConditionalOnProperty(name = "db.sql.pgsql.url")
    public PgsqlDataHelper pgsqlDataHelper() {
        return new PgsqlDataHelper(pgsql);
    }

    @Bean("pgsqlSecond")
    @ConditionalOnProperty(name = "db.sql.pgsql-second.url")
    public PgsqlDataHelper pgsqlSecond() {
        return new PgsqlDataHelper(pgsqlSecond);
    }

}
