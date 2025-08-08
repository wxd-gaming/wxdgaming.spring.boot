package wxdgaming.spring.logcenter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.batis.sql.pgsql.PgsqlConfiguration;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.MainApplicationContextProvider;
import wxdgaming.spring.boot.scheduled.ScheduledConfiguration;

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
                PgsqlConfiguration.class,
                ScheduledConfiguration.class,
                LogCenterApplication.class,
        }
)
public class LogCenterApplication {


    public static void main(String[] args) {
        log.info("日志中心启动中...");
        try {
            ConfigurableApplicationContext applicationContext = SpringApplication.run(LogCenterApplication.class, args);
            MainApplicationContextProvider bean = applicationContext.getBean(MainApplicationContextProvider.class);
            bean.start();
            log.info("日志中心启动完成...");
        } catch (Exception e) {
            log.debug("日志中心启动异常...", e);
            System.exit(99);
        }
    }

}
