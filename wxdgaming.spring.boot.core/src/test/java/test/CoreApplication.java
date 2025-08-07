package test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.executor.ExecutorConfig;

/**
 * 核心
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-28 20:21
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreConfiguration.class,
                ExecutorConfig.class
        }
)
public class CoreApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(CoreApplication.class, args);
    }

}
