package wxdgaming.game.login.inner.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import wxdgaming.game.login.LoginConfig;

/**
 * 拦截器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-11 15:20
 **/
@Slf4j
@Component
public class InnerFilter implements HandlerInterceptor {

    final LoginConfig loginConfig;

    public InnerFilter(LoginConfig loginConfig) {
        this.loginConfig = loginConfig;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 请求处理之前调用
        return true; // 返回 true 继续执行，false 中断执行
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 请求处理之后，视图渲染之前调用
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 整个请求完成之后调用
    }

//    @Override public Object doFilter(HttpRequest httpRequest, Method method, HttpContext httpContext) {
//        if (httpContext.getRequest().getUriPath().startsWith("/inner")) {
//            JSONObject reqParams = httpContext.getRequest().getReqParams();
//            Object sign = reqParams.remove("sign");
//            String json = reqParams.toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
//            String md5DigestEncode = Md5Util.md5DigestEncode0("#", json, loginConfig.getJwtKey());
//            if (!Objects.equals(sign, md5DigestEncode)) {
//                return RunResult.fail("签名错误");
//            }
//        }
//        return null;
//    }

}
