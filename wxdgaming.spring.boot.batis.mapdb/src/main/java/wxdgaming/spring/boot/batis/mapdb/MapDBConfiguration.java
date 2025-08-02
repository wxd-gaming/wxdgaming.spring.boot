package wxdgaming.spring.boot.batis.mapdb;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.InitPrint;

/**
 * MapDBConfiguration
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-27 10:13
 **/
@Getter
@Setter
@Configuration
@EnableConfigurationProperties({CoreConfiguration.class, MapDBConfiguration.class})
@ConfigurationProperties(prefix = "db.mapdb")
public class MapDBConfiguration implements InitPrint {

    private String path;

    @Bean
    @ConditionalOnProperty(name = "db.mapdb.path")
    public MapDBDataHelper mapDBDataHelper() {
        return new MapDBDataHelper(path);
    }

}
