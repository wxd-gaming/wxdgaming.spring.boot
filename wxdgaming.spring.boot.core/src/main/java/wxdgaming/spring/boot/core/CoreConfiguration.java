package wxdgaming.spring.boot.core;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.core.executor.ExecutorConfig;
import wxdgaming.spring.boot.core.executor.ExecutorFactory;

/**
 * 扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-28 20:11
 **/
@Getter
@Setter
@Configuration
@EnableConfigurationProperties({CoreConfiguration.class})
@ConfigurationProperties(prefix = "core")
public class CoreConfiguration implements InitPrint {

    private static class Lazy {
        private static CoreConfiguration instance = new CoreConfiguration();
    }

    public static CoreConfiguration getInstance() {
        return Lazy.instance;
    }

    private boolean enableAsmDebug = false;
    private ExecutorConfig basic;
    private ExecutorConfig logic;
    private ExecutorConfig virtual;

    @PostConstruct
    public void init() {
        ExecutorFactory.init(this);
        Lazy.instance = this;
    }

    public ExecutorConfig getBasic() {
        if (basic == null) {
            basic = ExecutorConfig.BASIC_INSTANCE.get();
        }
        return basic;
    }

    public ExecutorConfig getLogic() {
        if (logic == null) {
            logic = ExecutorConfig.LOGIC_INSTANCE.get();
        }
        return logic;
    }

    public ExecutorConfig getVirtual() {
        if (virtual == null) {
            virtual = ExecutorConfig.VIRTUAL_INSTANCE.get();
        }
        return virtual;
    }
}
