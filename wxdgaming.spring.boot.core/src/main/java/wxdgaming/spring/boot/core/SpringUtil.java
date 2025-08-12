package wxdgaming.spring.boot.core;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;
import wxdgaming.spring.boot.core.loader.ClassDirLoader;
import wxdgaming.spring.boot.core.loader.JavaCoderCompile;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Enumeration;

/**
 * spring 工具
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-07-30 14:52
 */
@Slf4j
public class SpringUtil implements InitPrint {

    /** sd */
    public static MainApplicationContextProvider mainApplicationContextProvider;
    public static ScriptApplicationContextProvider scriptApplicationContext;

    /**
     * 判断一个类是否有 Spring 核心注解
     *
     * @param clazz 要检查的类
     * @return true 如果该类上添加了相应的 Spring 注解；否则返回 false
     */
    public static boolean hasSpringAnnotation(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }
        // 是否是接口
        if (clazz.isInterface()) {
            return false;
        }
        // 是否是抽象类
        if (Modifier.isAbstract(clazz.getModifiers())) {
            return false;
        }

        try {
            if (
                    clazz.getAnnotation(Configuration.class) != null ||
                    clazz.getAnnotation(ConfigurationProperties.class) != null ||
                    clazz.getAnnotation(ConditionalOnProperty.class) != null ||
                    clazz.getAnnotation(Service.class) != null ||
                    clazz.getAnnotation(Component.class) != null ||
                    clazz.getAnnotation(ComponentScan.class) != null ||
                    clazz.getAnnotation(Repository.class) != null ||
                    clazz.getAnnotation(Controller.class) != null ||
                    clazz.getAnnotation(RestController.class) != null
            ) {
                return true;
            }
        } catch (Exception e) {
            log.error("出现异常：{}", e.getMessage());
        }
        return false;
    }

    public static JSONObject convertParameter(HttpServletRequest request) throws IOException {
        JSONObject reqParams = new JSONObject();
        // 处理 POST JSON 请求体
        if ("POST".equalsIgnoreCase(request.getMethod()) && request.getContentType().contains("application/json")) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            wrapper.getReader().readLine();
            String body = new String(wrapper.getContentAsByteArray(), wrapper.getCharacterEncoding());
            JSONObject bodyParams = JSONObject.parseObject(body);
            reqParams.putAll(bodyParams);
        }

        Enumeration<String> parameterNames = request.getParameterNames();

        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            reqParams.put(name, request.getParameter(name));
        }



        return reqParams;
    }

    public static String getCurrentUrl(HttpServletRequest request) {
        String scheme = request.getScheme();              // http
        String serverName = request.getServerName();     // hostname.com
        int serverPort = request.getServerPort();        // 80
        String contextPath = request.getContextPath();   // /mywebapp
        String servletPath = request.getServletPath();   // /servlet/MyServlet

        // Reconstruct original requesting URL
        StringBuilder url = new StringBuilder();
        url.append(scheme).append("://").append(serverName);

        // Include server port if it's not standard http/https port
        if (!((scheme.equals("http") && serverPort == 80) || (scheme.equals("https") && serverPort == 443))) {
            url.append(":").append(serverPort);
        }

        url.append(contextPath).append(servletPath);

        return url.toString();
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static ScriptApplicationContextProvider newChild4Jar(ConfigurableApplicationContext parent, ClassLoader parentClassLoad, Class<?> scan, String... jarPaths) {
        ClassDirLoader classLoader = ClassDirLoader.bootLib(parentClassLoad, jarPaths);
        return newChild(parent, scan, classLoader);
    }

    public static ScriptApplicationContextProvider newChild4JavaCode(ConfigurableApplicationContext parent, ClassLoader parentClassLoad, Class<?> scan, String javaCodePath, String... resourceUrls) throws Exception {
        ClassDirLoader classLoader = new JavaCoderCompile()
                .parentClassLoader(parentClassLoad)
                .compilerJava(javaCodePath)
                .classLoader("target/scripts");

        classLoader.addURL(resourceUrls);
        return newChild(parent, scan, classLoader);
    }

    public static ScriptApplicationContextProvider newChild4Classes(ConfigurableApplicationContext parent, ClassLoader parentClassLoad, Class<?> scan, String... urls) throws Exception {
        ClassDirLoader classLoader = new ClassDirLoader(parentClassLoad, urls);
        return newChild(parent, scan, classLoader);
    }

    public static ScriptApplicationContextProvider newChild(ConfigurableApplicationContext parent, Class<?> scan, ClassDirLoader classLoader) {
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

        ScriptApplicationContextProvider scriptApplicationContextProvider = new ScriptApplicationContextProvider();
        scriptApplicationContextProvider.setApplicationContext(childContext);
        SpringUtil.registerInstance(childContext, scriptApplicationContextProvider.getClass().getSimpleName(), scriptApplicationContextProvider, true);

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
        return scriptApplicationContextProvider;
    }


    /**
     * 注册一个bean
     *
     * @param beanClass bean 类
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2024-07-26 17:30
     */
    public static void registerBean(ConfigurableApplicationContext context, Class<?> beanClass) {
        registerBean(context, beanClass.getName(), beanClass);
    }

    /**
     * 注册一个bean
     *
     * @param beanClass bean 类
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2024-07-26 17:30
     */
    public static void registerBean(ConfigurableApplicationContext context, String name, Class<?> beanClass) {
        registerBean(context, name, beanClass, true);
    }

    /**
     * 注册bean
     *
     * @param name      bean name
     * @param beanClass bean class
     * @param removeOld 是否删除旧的bean
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2024-08-12 13:40
     */
    public static void registerBean(ConfigurableApplicationContext context, String name, Class<?> beanClass, boolean removeOld) {
        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        // 将有@spring注解的类交给spring管理
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(beanClass);
        BeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
        // 设置当前bean定义对象是单利的
        beanDefinition.setScope("singleton");

        if (removeOld && defaultListableBeanFactory.containsBeanDefinition(name)) {
            defaultListableBeanFactory.removeBeanDefinition(name);
        }

        // 获取bean工厂并转换为DefaultListableBeanFactory
        defaultListableBeanFactory.registerBeanDefinition(name, beanDefinition);

        log.debug("register bean {}, {}", name, beanClass);
    }

    /**
     * 注册一个实例对象
     *
     * @param instance 对象
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2024-07-26 17:30
     */
    public static void registerInstance(ConfigurableApplicationContext context, Object instance) {
        registerInstance(context, instance.getClass().getName(), instance);
    }

    /**
     * 注册一个实例对象
     *
     * @param name     对象名，beanName
     * @param instance 对象实例
     * @author wxd-gaming(無心道, 15388152619)
     * @version 2024-07-26 17:30
     */
    public static <T> void registerInstance(ConfigurableApplicationContext context, String name, T instance) {
        registerInstance(context, name, instance, true);
    }

    public static <T> void registerInstance(ConfigurableApplicationContext context, String name, T instance, boolean removeOld) {
        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        if (removeOld && defaultListableBeanFactory.containsBean(name)) {
            defaultListableBeanFactory.destroySingleton(name);
        }
        defaultListableBeanFactory.registerSingleton(name, instance);

        log.debug("register instance {}, {}", name, instance.getClass().getName());
    }

    public static void registerController(ApplicationContext context, String controllerBeanName) {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        try {
            unregisterController(context, controllerBeanName);
        } catch (Throwable e) {
            log.debug("unregister controllerBeanName={}", controllerBeanName, e);
        }
        try {
            // 注册Controller
            Method method = requestMappingHandlerMapping
                    .getClass()
                    .getSuperclass()
                    .getSuperclass().
                    getDeclaredMethod("detectHandlerMethods", Object.class);
            // 将private改为可使用
            method.setAccessible(true);
            method.invoke(requestMappingHandlerMapping, controllerBeanName);
            log.debug("register Controller {}", controllerBeanName);
        } catch (Throwable e) {
            log.debug("register controllerBeanName={}", controllerBeanName, e);
        }
    }

    /**
     * 去掉Controller的Mapping
     *
     * @param controllerBeanName 需要卸载的服务
     */
    public static void unregisterController(ApplicationContext context, String controllerBeanName) {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = (RequestMappingHandlerMapping) context.getBean("requestMappingHandlerMapping");
        final Object controller = context.getBean(controllerBeanName);
        final Class<?> targetClass = controller.getClass();
        ReflectionUtils.doWithMethods(
                targetClass,
                method -> {
                    try {
                        Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
                        Method createMappingMethod = RequestMappingHandlerMapping.class.getDeclaredMethod(
                                "getMappingForMethod",
                                Method.class,
                                Class.class
                        );
                        createMappingMethod.setAccessible(true);
                        RequestMappingInfo requestMappingInfo = (RequestMappingInfo)
                                createMappingMethod.invoke(requestMappingHandlerMapping, specificMethod, targetClass);
                        if (requestMappingInfo != null) {
                            requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
                        }
                    } catch (Throwable e) {
                        log.error("unregister controllerBeanName={}", controllerBeanName, e);
                    }
                },
                ReflectionUtils.USER_DECLARED_METHODS
        );
    }


}
