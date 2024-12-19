package wxdgaming.spring.boot.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * spring 容器 反射解析
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-19 21:10
 **/
@Slf4j
@Getter
public class SpringReflect implements InitPrint, ApplicationContextAware {

    /** 上下文对象实例 */
    private ConfigurableApplicationContext applicationContext;


    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        log.info("register applicationContext");
    }

    public SpringReflectContext springReflectContext() {
        return SpringReflectContext.build(applicationContext);
    }

}
