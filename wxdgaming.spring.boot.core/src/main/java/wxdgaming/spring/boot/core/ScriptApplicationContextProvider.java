package wxdgaming.spring.boot.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * ApplicationContext 持有者
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-29 14:37
 **/
@Slf4j
@Getter
public class ScriptApplicationContextProvider extends ApplicationContextProvider {

    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        super.setApplicationContext(applicationContext);
        SpringUtil.scriptApplicationContext = this;
    }
}
