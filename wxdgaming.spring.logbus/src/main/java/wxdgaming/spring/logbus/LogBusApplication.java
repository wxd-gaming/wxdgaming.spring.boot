package wxdgaming.spring.logbus;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.MainApplicationContextProvider;
import wxdgaming.spring.boot.net.httpclient.HttpClientConfiguration;

/**
 * 日子中心启动器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 15:02
 **/
@Slf4j
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreConfiguration.class,
                HttpClientConfiguration.class,
                LogBusApplication.class,
        }
)
public class LogBusApplication {

    public static void main(String[] args) {
        log.info("LogBus...");
        try {
            ConfigurableApplicationContext applicationContext =  new SpringApplicationBuilder(LogBusApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
            MainApplicationContextProvider applicationContextProvider = applicationContext.getBean(MainApplicationContextProvider.class);
            applicationContextProvider.start();
            log.info("LogBus启动完成...");
        } catch (Exception e) {
            log.debug("LogBus启动异常...", e);
            System.exit(99);
        }
    }

}
