package wxdgaming.spring.boot.starter.core.threading;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;
import wxdgaming.spring.boot.starter.core.lang.ObjectBase;

/**
 * 线程池配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 15:05
 **/
@Getter
@Setter
public class ExecutorConfig extends ObjectBase {

    @JSONField(ordinal = 1)
    private int coreSize;
    @JSONField(ordinal = 2)
    private int maxSize;
    @JSONField(ordinal = 3)
    private int maxQueueSize;

    public ExecutorConfig() {
    }

    public ExecutorConfig(int coreSize, int maxSize, int maxQueueSize) {
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.maxQueueSize = maxQueueSize;
    }
}
