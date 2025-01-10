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
import wxdgaming.spring.boot.core.InitPrint;

import java.util.Map;

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
@ConfigurationProperties("spring.jdbc3")
@ConditionalOnProperty("spring.jdbc3.config.url")
public class JdbcHelper3 implements InitPrint {

    DruidSourceConfig config;
    DruidDataSource dataSource;
    EntityManager entityManager;

    @Bean("jdbcContext3")
    public JdbcContext jdbcContext() {
        config.createDatabase();
        dataSource = config.toDataSource();
        entityManager = config.entityManager(dataSource, Map.of());
        return new JdbcContext(dataSource, entityManager);
    }

}
