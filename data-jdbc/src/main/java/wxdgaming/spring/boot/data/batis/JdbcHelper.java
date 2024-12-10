package wxdgaming.spring.boot.data.batis;

import jakarta.persistence.EntityManager;
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

import javax.sql.DataSource;

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

    @Bean
    @Primary
    @ConditionalOnMissingBean(JdbcContext.class)
    public JdbcContext jdbcContext(DataSource dataSource, EntityManager entityManager) {
        return new JdbcContext(dataSource, entityManager);
    }

}
