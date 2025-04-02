package wxdgaming.spring.boot.starter.core;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

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
    private ApplicationContext applicationContext;
    private SpringReflectContent springReflectContent;


    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        log.info("register applicationContext");
    }

    public SpringReflectContent getSpringReflectContent() {
        if (springReflectContent == null) {
            this.springReflectContent = new SpringReflectContent(applicationContext);
        }
        return springReflectContent;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name 参数传入要获取的实例的类名 首字母小写，这是默认的
     */
    public <T> T getBean(String name) {
        return (T) applicationContext.getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz 获取的实例的类
     * @param <T>   实例
     */
    public <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name  获取的实例的名称
     * @param clazz 获取的实例的类
     * @param <T>   实例
     */
    public <T> T getBean(String name, Class<T> clazz) {
        return applicationContext.getBean(name, clazz);
    }

}
