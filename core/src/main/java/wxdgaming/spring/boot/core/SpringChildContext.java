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
import wxdgaming.spring.boot.core.util.StringsUtil;

import javax.swing.*;
import java.util.Collection;

/**
 * 子容器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-09-16 18:20
 **/
@Getter
@Service
public class SpringChildContext implements InitPrint {


    public ConfigurableApplicationContext newChild4Jar(ClassLoader parentClassLoad, Class<?> scan, String jarPath) {
        ClassDirLoader classLoader = ClassDirLoader.bootLib(parentClassLoad, jarPath);
        return newChild(scan, classLoader);
    }

    public ConfigurableApplicationContext newChild4JavaCode(ClassLoader parentClassLoad, Class<?> scan, String javaCodePath, String... resourceUrls) throws Exception {
        ClassDirLoader classLoader = new JavaCoderCompile()
                .parentClassLoader(parentClassLoad)
                .compilerJava(javaCodePath)
                .classLoader("target/scripts");

        classLoader.addURL(resourceUrls);
        return newChild(scan, classLoader);
    }

    public ConfigurableApplicationContext newChild4Classes(ClassLoader parentClassLoad, Class<?> scan, String... urls) throws Exception {
        ClassDirLoader classLoader = new ClassDirLoader(parentClassLoad, urls);
        return newChild(scan, classLoader);
    }

    public ConfigurableApplicationContext newChild(Class<?> scan, ClassDirLoader classLoader) {
        Collection<Class<?>> values = classLoader.getLoadClassMap().values();
        // 创建子容器
        AnnotationConfigServletWebApplicationContext childContext = new AnnotationConfigServletWebApplicationContext();
        ConfigurableApplicationContext parent = SpringUtil.getIns().getApplicationContext();
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
            if (bean1.getClass().isAnnotationPresent(Controller.class) || bean1.getClass().isAnnotationPresent(RestController.class)) {
                SpringUtil.getIns().registerInstance(SpringUtil.getIns().getApplicationContext(), beanDefinitionName, bean1, true);
                SpringUtil.getIns().registerController(SpringUtil.getIns().getApplicationContext(), beanDefinitionName);
            }
        }
        SpringUtil.getIns().setChildContext(childContext);
        return childContext;
    }

}
