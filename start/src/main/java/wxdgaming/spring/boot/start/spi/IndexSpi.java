package wxdgaming.spring.boot.start.spi;

import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wxdgaming.spring.boot.start.AppSpringReflect;

import java.lang.reflect.InvocationTargetException;

/**
 * s
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-19 09:35
 **/
@RestController
@RequestMapping("/")
public class IndexSpi {

    @Autowired AppSpringReflect appSpringReflect;


    @RequestMapping("/**")
    public void all(HttpServletRequest request, @RequestBody JSONObject json) {
        System.out.println("all");
        String contextPath = request.getContextPath();/*假如xxx/index*/
        appSpringReflect.content().withMethodAnnotated(RequestMapping.class)
                .filter(tuple2 -> contextPath.startsWith(tuple2.getLeft().getClass().getAnnotation(RequestMapping.class).value()[0]))
                .filter(tuple2 -> contextPath.endsWith(tuple2.getRight().getAnnotation(RequestMapping.class).value()[0]))
                .forEach(tuple2 -> {
                    Object[] params = new Object[]{json};/*这里需要重新构建参数列表*/
                    try {
                        tuple2.getRight().invoke(tuple2.getLeft(), params);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    @RequestMapping("/index")
    public String index() {
        return "index";
    }

}
