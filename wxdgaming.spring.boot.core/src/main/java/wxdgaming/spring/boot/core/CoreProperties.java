package wxdgaming.spring.boot.core;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import wxdgaming.spring.boot.core.executor.ExecutorConfig;

/**
 * 核心配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-30 15:13
 **/
@Getter
@Setter
@ConfigurationProperties(prefix = "core.executor")
public class CoreProperties {

    private boolean enableAsmDebug = false;
    private ExecutorConfig basic;
    private ExecutorConfig logic;
    private ExecutorConfig virtual;


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
