package wxdgaming.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.MainApplicationContextProvider;
import wxdgaming.spring.boot.core.ann.Init;
import wxdgaming.spring.boot.core.ann.Start;

@Slf4j
@SpringBootApplication(
        scanBasePackageClasses = {
                CoreConfiguration.class,
                RunApplication.class,
        }
)
public class RunApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext applicationContext = SpringApplication.run(RunApplication.class, args);
        applicationContext.getBean(MainApplicationContextProvider.class).executorWithMethodAnnotatedIgnoreException(Init.class);
        applicationContext.getBean(MainApplicationContextProvider.class).executorWithMethodAnnotatedIgnoreException(Start.class);

    }

}
