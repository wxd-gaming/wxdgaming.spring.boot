package wxdgaming.spring.boot.webclient;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executor;

/**
 * http post json
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:21
 */
public class HttpPostJsonWork extends HttpWork {

    /** 参数 */
    protected final StringBuilder stringBuilder = new StringBuilder();

    public HttpPostJsonWork(Executor executor, CloseableHttpClient closeableHttpClient, String url) {
        super(executor, closeableHttpClient, url);
        contentType = ContentType.APPLICATION_JSON;
    }

    @Override protected HttpUriRequestBase httpUriRequest() {
        HttpPost httpPost = new HttpPost(getUrl());
        httpPost.setEntity(new StringEntity(stringBuilder.toString(), StandardCharsets.UTF_8));
        return httpPost;
    }

    /** 设置参数 */
    public HttpPostJsonWork setJson(String value) {
        stringBuilder.setLength(0);
        stringBuilder.append(value);
        return this;
    }

    /** 请求 */
    @Override public HttpPostJsonWork request() {
        super.request();
        return this;
    }

    @Override public HttpPostJsonWork addRequestHeader(String headerKey, String headerValue) {
        super.addRequestHeader(headerKey, headerValue);
        return this;
    }

    @Override public HttpPostJsonWork connectTimeOut(long connectTimeOut) {
        super.connectTimeOut(connectTimeOut);
        return this;
    }

    @Override public HttpPostJsonWork responseTimeout(long responseTimeout) {
        super.responseTimeout(responseTimeout);
        return this;
    }
}
