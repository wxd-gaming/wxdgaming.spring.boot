package wxdgaming.spring.boot.core;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 拦截器传递
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-12 16:19
 */
public class ContentCachingRequestWrapperFilter implements OrderedFilter {

    @Override
    public int getOrder() {
        //顺序控制要看你自己的代码
        //尽量小，比如说我这里是OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER-106
        //REQUEST_WRAPPER_FILTER_MAX_ORDER变量是spring 官方推荐的顺序
        //但是直接使用可能也会有坑，你可以自己查一下。
        //因为有一个spring boot 默认扩展的过滤OrderedRequestContextFilter
        //它使用的是REQUEST_WRAPPER_FILTER_MAX_ORDER - 105
        //所以为了尽可能早一点，你自己根据你的情况调整顺序
        return OrderedFilter.REQUEST_WRAPPER_FILTER_MAX_ORDER - 106;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        //传递包装类下去。这样后面的servlet等可以拿到这个包装后的request
        ContentCachingRequestWrapperNew requestWrapperNew = new ContentCachingRequestWrapperNew((HttpServletRequest) request);
        ServletInputStream inputStream = requestWrapperNew.getInputStream();
        /*TODO 这个代码不能删除，必须强制读取一次才行*/
//        String string = requestWrapperNew.getReader().readLine();
        chain.doFilter(requestWrapperNew, response);
    }

}
