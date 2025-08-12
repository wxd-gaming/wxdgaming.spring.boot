package run;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.MainApplicationContextProvider;
import wxdgaming.spring.boot.net.httpclient.HttpClientConfiguration;
import wxdgaming.spring.logbus.LogBusApplication;

/**
 * 日子中心启动器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 15:02
 **/
@Slf4j
@EnableScheduling
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreConfiguration.class,
                HttpClientConfiguration.class,
                LogBusApplication.class,
                LogBusApplicationTest.class,
        }
)
public class LogBusApplicationTest {

    public static void main(String[] args) {
        log.info("LogBus启动中...");
        try {
            ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(LogBusApplicationTest.class)
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
