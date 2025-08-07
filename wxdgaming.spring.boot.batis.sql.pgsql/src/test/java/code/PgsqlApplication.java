package code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlConfiguration;
import wxdgaming.spring.boot.core.ApplicationContextProvider;
import wxdgaming.spring.boot.core.ann.Start;

/**
 * 核心
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-28 20:21
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                PgsqlConfiguration.class
        }
)
public class PgsqlApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(PgsqlApplication.class, args);
        ApplicationContextProvider bean = run.getBean(ApplicationContextProvider.class);
        bean.withMethodAnnotated(Start.class).forEach(ApplicationContextProvider.MethodContent::invoke);
    }

}
