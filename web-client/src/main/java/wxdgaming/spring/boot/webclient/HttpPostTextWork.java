package wxdgaming.spring.boot.webclient;

import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * http post text
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:21
 */
public class HttpPostTextWork extends HttpPostMultiWork {

    public HttpPostTextWork(Executor executor, CloseableHttpClient closeableHttpClient, String url) {
        super(executor, closeableHttpClient, url);
        contentType = ContentType.APPLICATION_FORM_URLENCODED;
    }

    @Override protected HttpUriRequestBase httpUriRequest() {
        HttpPost httpPost = new HttpPost(getUrl());
        httpPost.setEntity(new StringEntity(param2String(), StandardCharsets.UTF_8));
        return httpPost;
    }

    protected String param2String() {
        StringBuilder stringBuilder = new StringBuilder();
        if (getParamMap() != null && !getParamMap().isEmpty()) {
            for (Map.Entry<String, Object> stringObjectEntry : getParamMap().entrySet()) {
                if (!stringBuilder.isEmpty()) {
                    stringBuilder.append("&");
                }
                stringBuilder.append(stringObjectEntry.getKey()).append("=").append(stringObjectEntry.getValue());
            }
        }
        return stringBuilder.toString();
    }

    /** 添加参数 */
    public HttpPostTextWork addRequestParam(String value) {
        String[] split = value.split("&");
        for (String string : split) {
            int indexOf = string.indexOf("=");
            String k = string.substring(0, indexOf);
            String v = string.substring(indexOf + 1);
            addRequestParam0(k, v);
        }
        return this;
    }

    @Override public HttpPostTextWork addRequestHeader(String headerKey, String headerValue) {
        super.addRequestHeader(headerKey, headerValue);
        return this;
    }

    /**
     * 添加参数
     *
     * @param params
     */
    @Override public HttpPostTextWork addRequestParams(Map<String, Object> params) {
        super.addRequestParams(params);
        return this;
    }

    /**
     * 添加参数
     *
     * @param params
     * @param urlEncode
     */
    @Override public HttpPostTextWork addRequestParams(Map<String, Object> params, boolean urlEncode) {
        super.addRequestParams(params, urlEncode);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     */
    @Override public HttpPostTextWork addRequestParam(String key, Object value) {
        super.addRequestParam(key, value);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @param urlEncode
     */
    @Override public HttpPostTextWork addRequestParam(String key, Object value, boolean urlEncode) {
        super.addRequestParam(key, value, urlEncode);
        return this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     */
    @Override protected void addRequestParam0(String key, Object value) {
        super.addRequestParam0(key, value);
    }

    /** 请求 */
    @Override public HttpPostTextWork request() {
        super.request();
        return this;
    }
}
