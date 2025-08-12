package wxdgaming.game.server;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.game.server.bean.BackendConfig;

/**
 * 驱动配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-07 18:09
 **/
@Configuration
public class GameServiceConfiguration  {


    @Bean public int serverType(@Value("${serverType}") int serverType) throws Throwable {
        return serverType;
    }

    @Bean
    public BackendConfig backendConfig(@Value("${backends}") BackendConfig backendConfig) {
        return backendConfig;
    }

}
