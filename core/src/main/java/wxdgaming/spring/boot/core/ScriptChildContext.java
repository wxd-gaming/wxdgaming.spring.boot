package wxdgaming.spring.boot.core;

import jakarta.servlet.ServletContext;
import lombok.Getter;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.spring.boot.core.loader.ClassDirLoader;
import wxdgaming.spring.boot.core.loader.JavaCoderCompile;

import java.util.Collection;

/**
 * 子容器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-16 18:20
 **/
@Getter
@Service
public class ScriptChildContext implements InitPrint {


    public ConfigurableApplicationContext newChild4Jar(ConfigurableApplicationContext parent, ClassLoader parentClassLoad, Class<?> scan, String... jarPaths) {
        ClassDirLoader classLoader = ClassDirLoader.bootLib(parentClassLoad, jarPaths);
        return newChild(parent, scan, classLoader);
    }

    public ConfigurableApplicationContext newChild4JavaCode(ConfigurableApplicationContext parent, ClassLoader parentClassLoad, Class<?> scan, String javaCodePath, String... resourceUrls) throws Exception {
        ClassDirLoader classLoader = new JavaCoderCompile()
                .parentClassLoader(parentClassLoad)
                .compilerJava(javaCodePath)
                .classLoader("target/scripts");

        classLoader.addURL(resourceUrls);
        return newChild(parent, scan, classLoader);
    }

    public ConfigurableApplicationContext newChild4Classes(ConfigurableApplicationContext parent, ClassLoader parentClassLoad, Class<?> scan, String... urls) throws Exception {
        ClassDirLoader classLoader = new ClassDirLoader(parentClassLoad, urls);
        return newChild(parent, scan, classLoader);
    }

    public ConfigurableApplicationContext newChild(ConfigurableApplicationContext parent, Class<?> scan, ClassDirLoader classLoader) {
        Collection<Class<?>> values = classLoader.getLoadClassMap().values();
        // 创建子容器
        AnnotationConfigServletWebApplicationContext childContext = new AnnotationConfigServletWebApplicationContext();
        childContext.setParent(parent);
        childContext.setEnvironment(parent.getEnvironment());
        childContext.setApplicationStartup(parent.getApplicationStartup());
        childContext.setServletContext(parent.getBean(ServletContext.class));
        childContext.setClassLoader(classLoader);
        // 设置扫描类
        childContext.register(scan);
        // 刷新子容器以完成初始化
        childContext.refresh();
        String[] beanDefinitionNames = childContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean1 = childContext.getBean(beanDefinitionName);
            if (!values.contains(bean1.getClass())) {
                continue;
            }
            /*把请求注入到主容器*/
            if (bean1.getClass().isAnnotationPresent(Controller.class) || bean1.getClass().isAnnotationPresent(RestController.class)) {
                SpringUtil.registerInstance(parent, beanDefinitionName, bean1, true);
                SpringUtil.registerController(parent, beanDefinitionName);
            }
        }
        return childContext;
    }


    public void unload() {

    }

}
