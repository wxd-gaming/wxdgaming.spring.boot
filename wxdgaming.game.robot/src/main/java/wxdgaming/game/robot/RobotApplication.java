package wxdgaming.game.robot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.game.login.LoginServiceConfiguration;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.MainApplicationContextProvider;
import wxdgaming.spring.boot.excel.DataConfiguration;
import wxdgaming.spring.boot.net.SocketConfiguration;
import wxdgaming.spring.boot.scheduled.ScheduledConfiguration;

/**
 * 启动器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-27 11:27
 **/
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreConfiguration.class,
                SocketConfiguration.class,
                DataConfiguration.class,
                ScheduledConfiguration.class,
                LoginServiceConfiguration.class,
                RobotApplication.class
        }
)
public class RobotApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext run = new SpringApplication(RobotApplication.class).run(args);
        MainApplicationContextProvider applicationContextProvider = run.getBean(MainApplicationContextProvider.class);
        applicationContextProvider.start();

    }

}
