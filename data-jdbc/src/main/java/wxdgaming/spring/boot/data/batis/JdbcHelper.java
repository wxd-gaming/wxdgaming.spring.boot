package wxdgaming.spring.boot.data.batis;

import com.alibaba.druid.pool.DruidDataSource;
import jakarta.persistence.EntityManager;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wxdgaming.spring.boot.core.InitPrint;

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
@ConfigurationProperties("spring.jdbc")
@ConditionalOnProperty("spring.jdbc.config.url")
public class JdbcHelper implements InitPrint {

    DruidSourceConfig config;

    @Bean
    @Primary
    public DruidDataSource datasource() {
        config.createDatabase();
        return config.toDataSource();
    }

    @Bean
    @Primary
    public JdbcContext jdbcContext(DruidDataSource dataSource, EntityManager entityManager) {
        return new JdbcContext(dataSource, entityManager);
    }

}
