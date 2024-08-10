package wxdgaming.spring.boot.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wxdgaming.spring.boot.core.LogbackUtil;

/**
 * 过滤器 , 在类使用 注解 {@link RequestMapping}
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-07-30 13:59
 **/
public interface BaseFilter extends WebMvcConfigurer, HandlerInterceptor {

    @Override
    default void addInterceptors(InterceptorRegistry registry) {
        try {
            RequestMapping annotation = this.getClass().getAnnotation(RequestMapping.class);
            String[] value = annotation.value();
            String string = value[0];
            InterceptorRegistration interceptorRegistration = registry.addInterceptor(this).addPathPatterns(string);
            LogbackUtil.logger().info("{} addPathPatterns {}", this.getClass().getSimpleName(), string);
            filter(interceptorRegistration);
        } catch (Throwable e) {
            LogbackUtil.logger().error("添加过滤器", e);
        }
    }

    void filter(InterceptorRegistration registration);

    default String getCurrentUrl(HttpServletRequest request) {
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

    @Override default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            LogbackUtil.logger().info(
                    "\n{} {}\ndata={}\nhandler={}",
                    request.getMethod(),
                    getCurrentUrl(request),
                    request.getQueryString(),
                    handler,
                    ex
            );
        }
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
