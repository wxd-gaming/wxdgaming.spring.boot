package wxdgaming.spring.boot.batis.sql.pgsql;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
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
@Configuration
@EnableConfigurationProperties({CoreConfiguration.class, PgsqlConfiguration.class})
@ConfigurationProperties(prefix = "db.sql")
public class PgsqlConfiguration implements InitPrint {

    private SqlConfig pgsql;

    @PostConstruct
    public void init() {
        System.out.println(1);
    }

    @Bean
    public PgsqlDataHelper pgsqlDataHelper() {
        return new PgsqlDataHelper(pgsql);
    }

}
