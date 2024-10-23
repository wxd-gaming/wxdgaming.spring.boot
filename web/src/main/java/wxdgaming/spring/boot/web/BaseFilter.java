package wxdgaming.spring.boot.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wxdgaming.spring.boot.core.LogbackUtil;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.web.service.ResponseService;

/**
 * 过滤器 , 在类使用 注解 {@link RequestMapping}
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-07-30 13:59
 **/
public abstract class BaseFilter implements WebMvcConfigurer, HandlerInterceptor {

    @Autowired ResponseService responseService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
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

    public abstract void filter(InterceptorRegistration registration);


    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
    }

    @Override public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            LogbackUtil.logger().info(
                    "\n{} {}\ndata={}\nhandler={}",
                    request.getMethod(),
                    SpringUtil.getCurrentUrl(request),
                    request.getQueryString(),
                    handler,
                    ex
            );
        }
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
