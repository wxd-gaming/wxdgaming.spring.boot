package wxdgaming.game.login;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
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
                HttpClientConfiguration.class,
                SocketConfiguration.class,
                ScheduledConfiguration.class,
                LoginApplication.class
        }
)
public class LoginApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = new SpringApplication(LoginApplication.class).run(args);
        MainApplicationContextProvider applicationContextProvider = run.getBean(MainApplicationContextProvider.class);
        applicationContextProvider.start();
    }

}
