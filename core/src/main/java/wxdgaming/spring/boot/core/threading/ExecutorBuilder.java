package wxdgaming.spring.boot.core.threading;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import wxdgaming.spring.boot.core.InitPrint;

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
public class ExecutorBuilder implements InitPrint {

    /** 核心线程数量 */
    private int defaultCoreSize = 2;
    /** 逻辑线程数量 */
    private int logicCoreSize = 10;
    /** 虚拟线程并发数量 */
    private int virtualCoreSize = 100;

    @Bean
    @Primary
    public DefaultExecutor defaultExecutor() {
        return new DefaultExecutor(defaultCoreSize);
    }

    /** 逻辑线程池 */
    @Bean
    @Primary
    public LogicExecutor logicExecutor() {
        return new LogicExecutor(logicCoreSize);
    }

    /** 虚拟线程池 */
    @Bean
    @Primary
    public VirtualExecutor virtualExecutor() {
        return new VirtualExecutor(virtualCoreSize);
    }
}
