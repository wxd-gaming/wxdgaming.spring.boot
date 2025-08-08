package run;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.MainApplicationContextProvider;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.boot.net.httpclient.HttpClientConfiguration;
import wxdgaming.spring.logbus.LogBusApplication;
import wxdgaming.spring.logbus.LogBusService;
import wxdgaming.spring.logbus.LogEntity;

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
public class LogBusApplicationTest {

    static final HexId HEX_ID = new HexId(1);

    public static void main(String[] args) {
        log.info("LogBus启动中...");
        try {
            ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(LogBusApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
            MainApplicationContextProvider applicationContextProvider = applicationContext.getBean(MainApplicationContextProvider.class);
            applicationContextProvider.start();
            log.info("LogBus启动完成...");

            LogBusService logBusService = applicationContext.getBean(LogBusService.class);

            for (int i = 0; i < 1000; i++) {
                LogEntity logEntity = new LogEntity();
                logEntity.setUid(HEX_ID.newId());
                logEntity.setCreateTime(System.currentTimeMillis());
                logEntity.setLogType("login");
                logEntity.putLogData("account", StringUtils.randomString(8));
                logBusService.addLog(logEntity);
            }

        } catch (Exception e) {
            log.debug("LogBus启动异常...", e);
            System.exit(99);
        }
    }

}
