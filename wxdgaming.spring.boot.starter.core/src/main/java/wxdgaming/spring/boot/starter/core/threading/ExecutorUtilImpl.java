package wxdgaming.spring.boot.starter.core.threading;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import wxdgaming.spring.boot.starter.core.system.AnnUtil;
import wxdgaming.spring.boot.starter.core.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 异步化处理
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2023-12-04 19:23
 **/
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "executor")
public class ExecutorUtilImpl {

    @Getter static ExecutorUtil instance;

    ExecutorConfig basic = new ExecutorConfig(2, 2, 5000);
    ExecutorConfig logic = new ExecutorConfig(4, 16, 5000);
    ExecutorConfig virtual = new ExecutorConfig(100, 100, 5000);

    @Bean
    public ExecutorUtil init() {
        instance = new ExecutorUtil();
        instance.init(basic, logic, virtual);
        return instance;
    }

    public static void action(AtomicBoolean vt,
                              AtomicReference<String> threadName,
                              AtomicReference<String> queueName,
                              Method method) {
        ExecutorWith ann = AnnUtil.ann(method, ExecutorWith.class);
        if (ann != null) {
            vt.set(ann.useVirtualThread());
            if (StringUtils.isNotBlank(ann.threadName())) {
                threadName.set(ann.threadName());
            }
            if (StringUtils.isNotBlank(ann.queueName())) {
                queueName.set(ann.queueName());
            }
        }
    }

}
