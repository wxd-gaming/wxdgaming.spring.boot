package wxdgaming.game.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.MainApplicationContextProvider;
import wxdgaming.spring.boot.net.SocketConfiguration;
import wxdgaming.spring.boot.scheduled.ScheduledConfiguration;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-27 20:56
 **/
@ConfigurationPropertiesScan(basePackageClasses = {ChatBootstrapConfig.class})
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreConfiguration.class,
                SocketConfiguration.class,
                ScheduledConfiguration.class,
                ChatApplication.class,
        }
)
public class ChatApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(ChatApplication.class, args);
        MainApplicationContextProvider applicationContextProvider = applicationContext.getBean(MainApplicationContextProvider.class);
        applicationContextProvider.start();
    }

}
