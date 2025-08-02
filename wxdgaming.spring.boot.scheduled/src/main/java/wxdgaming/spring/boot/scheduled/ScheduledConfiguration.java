package wxdgaming.spring.boot.scheduled;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.executor.ExecutorConfig;
import wxdgaming.spring.boot.core.executor.QueuePolicyConst;

/**
 * 定时器注入器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-30 10:48
 */
@Slf4j
@Getter
@Setter
@ComponentScan(basePackageClasses = {CoreConfiguration.class})
@Configuration
@ConfigurationProperties(prefix = "core.executor")
@EnableConfigurationProperties
public class ScheduledConfiguration implements InitPrint {

    private ExecutorConfig scheduled;

    @Bean
    public ScheduledService scheduledService() {
        if (scheduled == null) {
            log.debug("scheduled is null");
            scheduled = new ExecutorConfig().setCoreSize(1).setMaxQueueSize(1000).setQueuePolicy(QueuePolicyConst.AbortPolicy);
        }
        return new ScheduledService(scheduled);
    }

}
