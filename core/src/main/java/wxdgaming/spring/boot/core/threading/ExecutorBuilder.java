package wxdgaming.spring.boot.core.threading;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 线程类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 13:57
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties("server.executor")
public class ExecutorBuilder {

    /** 核心线程数量 */
    private int defaultCoreSize = 2;
    /** 逻辑线程数量 */
    private int logicCoreSize = 10;
    /** 虚拟线程并发数量 */
    private int virtualCoreSize = 100;

    @Bean
    @ConditionalOnMissingBean(DefaultExecutor.class)
    public DefaultExecutor defaultExecutor() {
        return new DefaultExecutor(defaultCoreSize);
    }

    /** 逻辑线程池 */
    @Bean
    @ConditionalOnMissingBean(LogicExecutor.class)
    public LogicExecutor logicExecutor() {
        return new LogicExecutor(logicCoreSize);
    }

    /** 虚拟线程池 */
    @Bean
    @ConditionalOnMissingBean(VirtualExecutor.class)
    public VirtualExecutor virtualExecutor() {
        return new VirtualExecutor(virtualCoreSize);
    }
}
