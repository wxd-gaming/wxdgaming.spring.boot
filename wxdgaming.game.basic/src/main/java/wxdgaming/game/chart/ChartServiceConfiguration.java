package wxdgaming.game.chart;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 驱动配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-07 18:09
 **/
@Configuration
public class ChartServiceConfiguration {


    @Bean
    public ChartConfig chartConfig(@Value("${chart}") ChartConfig config) throws Throwable {
        return config;
    }

}
