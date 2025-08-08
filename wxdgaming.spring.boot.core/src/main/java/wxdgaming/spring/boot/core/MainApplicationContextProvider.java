package wxdgaming.spring.boot.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.ann.Init;
import wxdgaming.spring.boot.core.ann.Shutdown;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.executor.ExecutorFactory;
import wxdgaming.spring.boot.core.util.JvmUtil;

import java.io.Closeable;

/**
 * ApplicationContext 持有者
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-07-29 14:37
 **/
@Slf4j
@Getter
@Component
public class MainApplicationContextProvider extends ApplicationContextProvider {

    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        SpringUtil.mainApplicationContextProvider = this;
    }

    public void start() {
        executorWithMethodAnnotated(Init.class);
        executorWithMethodAnnotated(Start.class);
        JvmUtil.addShutdownHook(() -> {
            executorWithMethodAnnotatedIgnoreException(Shutdown.class);
            classWithSuper(AutoCloseable.class).forEach(bean -> {
                if (bean instanceof Closeable) {
                    try {
                        ((Closeable) bean).close();
                    } catch (Exception e) {
                        log.error("关闭bean异常...", e);
                    }
                }
            });
            ExecutorFactory.getEXECUTOR_MONITOR().getExit().set(true);
            JvmUtil.halt(0);
        });
    }

}
