package wxdgaming.spring.boot.core;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.executor.ExecutorFactory;

/**
 * 扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-28 20:11
 **/
@Getter
@Order(Ordered.HIGHEST_PRECEDENCE)
@Component
@EnableConfigurationProperties(CoreProperties.class)
public class CoreConfiguration implements InitPrint {

    private final CoreProperties coreProperties;

    @Autowired
    public CoreConfiguration(CoreProperties coreProperties) {
        this.coreProperties = coreProperties;
    }

    @PostConstruct
    public void init() {
        ExecutorFactory.init(this.getCoreProperties());
    }


}
