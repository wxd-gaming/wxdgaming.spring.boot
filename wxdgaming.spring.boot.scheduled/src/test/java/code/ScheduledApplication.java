package code;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.core.ApplicationContextProvider;
import wxdgaming.spring.boot.core.ann.Init;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.scheduled.ScheduledConfiguration;

@Slf4j
@SpringBootApplication(scanBasePackageClasses = {
        ScheduledConfiguration.class,
        ScheduledApplication.class
})
public class ScheduledApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ScheduledApplication.class, args);
        ApplicationContextProvider bean = run.getBean(ApplicationContextProvider.class);
        bean.withMethodAnnotated(Init.class).forEach(ApplicationContextProvider.MethodContent::invoke);
        bean.withMethodAnnotated(Start.class).forEach(ApplicationContextProvider.MethodContent::invoke);
    }

}
