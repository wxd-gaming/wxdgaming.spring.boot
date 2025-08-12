package wxdgaming.game.robot;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.MainApplicationContextProvider;
import wxdgaming.spring.boot.excel.DataConfiguration;
import wxdgaming.spring.boot.net.SocketConfiguration;
import wxdgaming.spring.boot.scheduled.ScheduledConfiguration;

/**
 * 启动器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-27 11:27
 **/
@ConfigurationPropertiesScan(basePackageClasses = {RobotBootstrapConfig.class})
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreConfiguration.class,
                SocketConfiguration.class,
                DataConfiguration.class,
                ScheduledConfiguration.class,
                RobotApplication.class
        }
)
public class RobotApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext run = new SpringApplicationBuilder(RobotApplication.class)
                .web(WebApplicationType.NONE)
                .run(args);
        MainApplicationContextProvider applicationContextProvider = run.getBean(MainApplicationContextProvider.class);
        applicationContextProvider.start();

    }

}
