package wxdgaming.spring.boot.webclient;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

/**
 * http get
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:21
 */
public class HttpGetAction extends HttpAction<HttpGetAction> {

    public HttpGetAction(CloseableHttpClient closeableHttpClient, String url) {
        super(closeableHttpClient, url);
    }

    @Override protected HttpUriRequestBase httpUriRequest() {
        HttpGet httpGet;
        if (getParamMap() != null && !getParamMap().isEmpty()) {
            String param2String = param2String();
            httpGet = new HttpGet(getUrl() + "?" + param2String);
        } else {
            httpGet = new HttpGet(getUrl());
        }
        return httpGet;
    }

}
