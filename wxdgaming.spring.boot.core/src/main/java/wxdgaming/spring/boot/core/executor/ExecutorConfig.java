package wxdgaming.spring.boot.core.executor;

import com.alibaba.fastjson.annotation.JSONCreator;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.lang.ObjectBase;

import java.util.function.Supplier;

/**
 * 线程池配置
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-13 15:05
 **/
@Getter
@Setter
@Accessors(chain = true)
public class ExecutorConfig extends ObjectBase {

    public static Supplier<ExecutorConfig> BASIC_INSTANCE = () -> new ExecutorConfig().setCoreSize(2).setMaxQueueSize(5000).setQueuePolicy(QueuePolicyConst.AbortPolicy);
    public static Supplier<ExecutorConfig> LOGIC_INSTANCE = () -> new ExecutorConfig().setCoreSize(8).setMaxQueueSize(10000).setQueuePolicy(QueuePolicyConst.AbortPolicy);
    public static Supplier<ExecutorConfig> VIRTUAL_INSTANCE = () -> new ExecutorConfig().setCoreSize(100).setMaxQueueSize(20000).setQueuePolicy(QueuePolicyConst.AbortPolicy);

    @JSONField(ordinal = 1)
    private int coreSize;
    @JSONField(ordinal = 2)
    private int maxQueueSize;
    @JSONField(ordinal = 3)
    private QueuePolicyConst queuePolicy;

}
