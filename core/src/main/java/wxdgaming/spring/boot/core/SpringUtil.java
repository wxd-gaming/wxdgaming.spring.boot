package wxdgaming.spring.boot.core;

import com.alibaba.fastjson.JSONObject;
import io.netty.handler.codec.http.HttpHeaderValues;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import wxdgaming.spring.boot.core.lang.Tuple2;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Stream;

/**
 * spring 工具
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-07-30 14:52
 */
@Slf4j
@Getter
@Service
public class SpringUtil implements InitPrint, ApplicationContextAware {

    @Getter private static SpringUtil ins;

    public static final Comparator<Object> OBJECT_COMPARATOR = (o1, o2) -> {
        int o1Annotation = Optional.ofNullable(o1.getClass().getAnnotation(Order.class)).map(Order::value).orElse(999999);
        int o2Annotation = Optional.ofNullable(o2.getClass().getAnnotation(Order.class)).map(Order::value).orElse(999999);
        if (o1Annotation != o2Annotation) {
            return Integer.compare(o1Annotation, o2Annotation);
        }
        return o1.getClass().getName().compareTo(o2.getClass().getName());
    };

    public static final Comparator<Tuple2<?, Method>> METHOD_COMPARATOR = (o1, o2) -> {
        int o1Annotation = Optional.ofNullable(o1.getRight().getAnnotation(Order.class)).map(Order::value).orElse(999999);
        int o2Annotation = Optional.ofNullable(o2.getRight().getAnnotation(Order.class)).map(Order::value).orElse(999999);
        if (o1Annotation != o2Annotation) {
            return Integer.compare(o1Annotation, o2Annotation);
        }
        return o1.getRight().getName().compareTo(o2.getRight().getName());
    };


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

    /** 上下文对象实例 */
    private ConfigurableApplicationContext applicationContext;

    /** 上下文对象实例 */
    @Setter protected ConfigurableApplicationContext childContext;

    public SpringUtil() {
        ins = this;
    }

    @Override public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = (ConfigurableApplicationContext) applicationContext;
        log.info("register applicationContext");
    }

    public ConfigurableApplicationContext curApplicationContext() {
        if (childContext != null) return childContext;
        return applicationContext;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name 参数传入要获取的实例的类名 首字母小写，这是默认的
     */
    public <T> T getBean(String name) {
        return (T) curApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz
     * @param <T>
     */
    public <T> T getBean(Class<T> clazz) {
        return curApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name
     * @param clazz
     * @param <T>
     */
    public <T> T getBean(String name, Class<T> clazz) {
        return curApplicationContext().getBean(name, clazz);
    }

    public <T> Stream<T> getBeansOfType(@Nullable Class<T> type) {
        return curApplicationContext()
                .getBeansOfType(type)
                .values()
                .stream();
    }

    public Stream<Object> getBeans() {
        return SpringReflectContext.getBeans(curApplicationContext());
    }

    public Stream<Object> getBeansWithAnnotation(Class<? extends Annotation> annotation) {
        return curApplicationContext().getBeansWithAnnotation(annotation).values().stream();
    }

    /**
     * 返回当前 spring 容器所有的bean
     *
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-12 13:38
     */
    public SpringReflectContext reflectContext() {
        return SpringReflectContext.build(curApplicationContext());
    }

    /**
     * 指定注解的方法
     *
     * @param annotationType 注解
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-12 13:40
     */
    public Stream<Tuple2<Object, Method>> withMethodAnnotated(Class<? extends Annotation> annotationType) {
        return reflectContext()
                .withMethodAnnotated(annotationType)
                .sorted(METHOD_COMPARATOR);
    }

    public void executor(Class<? extends Annotation> annotationType) {
        withMethodAnnotated(annotationType)
                .forEach(t -> {
                    try {
                        t.getRight().setAccessible(true);
                        Object[] array = Arrays.stream(t.getRight().getParameterTypes()).map(curApplicationContext()::getBean).toArray();
                        t.getRight().invoke(t.getLeft(), array);
                    } catch (Exception e) {
                        throw new RuntimeException(t.getLeft().getClass().getName() + "#" + t.getRight().getName(), e);
                    }
                });
    }

    /**
     * 注册一个bean
     *
     * @param beanClass bean 类
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-07-26 17:30
     */
    public void registerBean(ConfigurableApplicationContext context, Class<?> beanClass) {
        registerBean(context, beanClass.getName(), beanClass);
    }

    /**
     * 注册一个bean
     *
     * @param beanClass bean 类
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-07-26 17:30
     */
    public void registerBean(ConfigurableApplicationContext context, String name, Class<?> beanClass) {
        registerBean(context, name, beanClass, true);
    }

    /**
     * 注册bean
     *
     * @param name      bean name
     * @param beanClass bean class
     * @param removeOld 是否删除旧的bean
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-12 13:40
     */
    public void registerBean(ConfigurableApplicationContext context, String name, Class<?> beanClass, boolean removeOld) {
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
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-07-26 17:30
     */
    public void registerInstance(ConfigurableApplicationContext context, Object instance) {
        registerInstance(context, instance.getClass().getName(), instance);
    }

    /**
     * 注册一个实例对象
     *
     * @param name     对象名，beanName
     * @param instance 对象实例
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-07-26 17:30
     */
    public <T> void registerInstance(ConfigurableApplicationContext context, String name, T instance) {
        registerInstance(context, name, instance, true);
    }

    public <T> void registerInstance(ConfigurableApplicationContext context, String name, T instance, boolean removeOld) {
        // 获取bean工厂并转换为DefaultListableBeanFactory
        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) context.getBeanFactory();
        if (removeOld && defaultListableBeanFactory.containsBean(name)) {
            defaultListableBeanFactory.destroySingleton(name);
        }
        defaultListableBeanFactory.registerSingleton(name, instance);

        log.debug("register instance {}, {}", name, instance.getClass().getName());
    }


    public void initHandlerMethods(ApplicationContext context) {
        final RequestMappingHandlerMapping requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        try {
            // 注册Controller
            Method method = requestMappingHandlerMapping
                    .getClass()
                    .getSuperclass()
                    .getSuperclass().
                    getDeclaredMethod("initHandlerMethods");
            // 将private改为可使用
            method.setAccessible(true);
            method.invoke(requestMappingHandlerMapping);
            log.debug("initHandlerMethods");
        } catch (Throwable e) {
            log.debug("initHandlerMethods", e);
        }
    }

    public void registerController(ApplicationContext context, String controllerBeanName) {
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
    public void unregisterController(ApplicationContext context, String controllerBeanName) {
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

    public static String getClientIp() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        if (!(requestAttributes instanceof ServletRequestAttributes servletRequestAttributes)) {
            return null;
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
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

    /** 获取报文 */
    public static String getRequestBody(HttpServletRequest request) throws IOException {
        if (Objects.equals(request.getMethod(), "GET")) {
            return request.getQueryString();
        } else {
            ServletInputStream inputStream = request.getInputStream();
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }

    /** 获取报文 */
    public static JSONObject getParameters(HttpServletRequest request) throws IOException {
        if (Objects.equals(request.getContentType(), HttpHeaderValues.APPLICATION_JSON.toString())) {
            return JSONObject.parseObject(getRequestBody(request));
        } else {
            Enumeration<String> parameterNames = request.getParameterNames();
            JSONObject jsonObject = new JSONObject();
            while (parameterNames.hasMoreElements()) {
                String name = parameterNames.nextElement();
                jsonObject.put(name, request.getParameter(name));
            }
            return jsonObject;
        }
    }

    /**
     * 记录请求日志
     *
     * @param request
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-12 16:26
     */
    public static void recordRequest(HttpServletRequest request) throws IOException {
        if (log.isDebugEnabled()) {
            StringBuilder stringBuilder = new StringBuilder().append("\n\n");
            stringBuilder.append(request.getMethod()).append(" ").append(getCurrentUrl(request)).append("\n");
            stringBuilder.append("servlet path: ").append(request.getServletPath()).append("\n");
            String header = request.getHeader("content-type");
            if (header != null) {
                stringBuilder.append("content-type: ").append(header).append("\n");
            }
            // stringBuilder.append("param data: ").append(getRequestBody(request)).append("\n");
            log.debug(stringBuilder.toString());
        }
    }

}
