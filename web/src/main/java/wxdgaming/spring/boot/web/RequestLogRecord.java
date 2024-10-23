package wxdgaming.spring.boot.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import wxdgaming.spring.boot.core.SpringUtil;

/**
 * 请求日志记录
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 13:17
 **/
@Service
@RequestMapping("/**")
public class RequestLogRecord extends BaseFilter {

    @Override public void filter(InterceptorRegistration registration) {

    }

    /** 记录请求日志 */
    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        SpringUtil.recordRequest(request);
        return super.preHandle(request, response, handler);
    }

}
