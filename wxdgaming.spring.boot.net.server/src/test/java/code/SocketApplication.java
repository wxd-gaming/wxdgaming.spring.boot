package code;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.ann.Init;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.net.SocketConfiguration;

@SpringBootApplication(scanBasePackageClasses = {
        CoreConfiguration.class,
        SocketConfiguration.class,
        SocketApplication.class,
})
public class SocketApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SocketApplication.class, args);
        SpringUtil.mainApplicationContextProvider.executorWithMethodAnnotated(Init.class);
        SpringUtil.mainApplicationContextProvider.executorWithMethodAnnotated(Start.class);
    }

}
