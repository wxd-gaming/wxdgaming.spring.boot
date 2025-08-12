package wxdgaming.game.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.MainApplicationContextProvider;
import wxdgaming.spring.boot.net.SocketConfiguration;
import wxdgaming.spring.boot.net.httpclient.HttpClientConfiguration;
import wxdgaming.spring.boot.scheduled.ScheduledConfiguration;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-27 20:56
 **/
@Slf4j
@ConfigurationPropertiesScan(basePackageClasses = {GatewayBootstrapConfig.class})
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreConfiguration.class,
                SocketConfiguration.class,
                HttpClientConfiguration.class,
                ScheduledConfiguration.class,
                GatewayApplication.class
        }
)
public class GatewayApplication {

    public static void main(String[] args) {
        try {
            ConfigurableApplicationContext run = new SpringApplicationBuilder(GatewayApplication.class)
                    .web(WebApplicationType.NONE)
                    .run(args);
            MainApplicationContextProvider applicationContextProvider = run.getBean(MainApplicationContextProvider.class);
            applicationContextProvider.start();
        } catch (BeansException e) {
            log.error("启动异常...", e);
            System.exit(99);
        }
    }

}
