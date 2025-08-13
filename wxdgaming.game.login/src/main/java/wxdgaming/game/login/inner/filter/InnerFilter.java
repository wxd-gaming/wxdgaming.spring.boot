package wxdgaming.game.login.inner.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.login.LoginBootstrapConfig;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.WebFilter;
import wxdgaming.spring.boot.core.io.Objects;
import wxdgaming.spring.boot.core.lang.AssertException;
import wxdgaming.spring.boot.core.util.Md5Util;

/**
 * 拦截器
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-06-11 15:20
 **/
@Slf4j
@Component
public class InnerFilter implements WebFilter {

    final LoginBootstrapConfig loginBootstrapConfig;

    public InnerFilter(LoginBootstrapConfig loginBootstrapConfig) {
        this.loginBootstrapConfig = loginBootstrapConfig;
    }

    @Override public String filterPath() {
        return "/inner/**";
    }

    @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String body = SpringUtil.readBody(request);
        JSONObject parameter = JSON.parseObject(body);
        Object sign = parameter.remove("sign");
        String json = parameter.toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
        String md5DigestEncode = Md5Util.md5DigestEncode0("#", json, loginBootstrapConfig.getJwtKey());
        if (!Objects.equals(sign, md5DigestEncode)) {
            throw new AssertException("签名错误");
        }
        return true;
    }

}
