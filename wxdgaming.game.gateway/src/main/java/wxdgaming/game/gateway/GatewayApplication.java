package wxdgaming.game.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.game.login.LoginServiceConfiguration;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.MainApplicationContextProvider;
import wxdgaming.spring.boot.net.SocketConfiguration;
import wxdgaming.spring.boot.net.httpclient.HttpClientConfiguration;
import wxdgaming.spring.boot.scheduled.ScheduledConfiguration;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-05-27 20:56
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreConfiguration.class,
                SocketConfiguration.class,
                HttpClientConfiguration.class,
                ScheduledConfiguration.class,
                LoginServiceConfiguration.class,
                GatewayApplication.class
        }
)
public class GatewayApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = new SpringApplication(GatewayApplication.class).run(args);
        MainApplicationContextProvider applicationContextProvider = run.getBean(MainApplicationContextProvider.class);
        applicationContextProvider.start();
    }

}
