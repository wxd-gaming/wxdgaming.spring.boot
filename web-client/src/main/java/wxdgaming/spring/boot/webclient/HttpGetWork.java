package wxdgaming.spring.boot.webclient;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import wxdgaming.spring.boot.core.util.StringsUtil;

import java.util.concurrent.Executor;

/**
 * http get
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:21
 */
public class HttpGetWork extends HttpPostTextWork {

    public HttpGetWork(Executor executor, CloseableHttpClient closeableHttpClient, String url) {
        super(executor, closeableHttpClient, url);
    }

    @Override protected HttpUriRequestBase httpUriRequest() {
        HttpGet httpGet;
        String param2String = param2String();
        if (StringsUtil.notEmptyOrNull(param2String)) {
            httpGet = new HttpGet(getUrl() + "?" + param2String);
        } else {
            httpGet = new HttpGet(getUrl());
        }
        return httpGet;
    }

    /** 请求 */
    @Override public HttpGetWork request() {
        super.request();
        return this;
    }

}
