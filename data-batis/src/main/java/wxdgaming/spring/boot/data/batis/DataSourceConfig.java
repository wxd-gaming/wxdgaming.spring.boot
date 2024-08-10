package wxdgaming.spring.boot.data.batis;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;

import javax.sql.DataSource;

/**
 * 数据源配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-02 21:19
 **/
@Service
@ConditionalOnProperty("spring.datasource.url")
public class DataSourceConfig implements InitPrint {

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSource datasource() {
        return DruidDataSourceBuilder.create().build();
    }
}
