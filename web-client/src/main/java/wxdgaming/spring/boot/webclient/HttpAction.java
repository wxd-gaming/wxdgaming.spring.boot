package wxdgaming.spring.boot.webclient;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import wxdgaming.spring.boot.core.json.FastJsonUtil;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * http 执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 17:33
 **/
@Slf4j
@Getter
public abstract class HttpAction<H extends HttpAction> {

    private final CloseableHttpClient closeableHttpClient;
    private final String url;
    protected ContentType contentType = ContentType.APPLICATION_FORM_URLENCODED;
    private final Map<String, String> requestHeaders = new LinkedHashMap<>();
    private ClassicHttpResponse response;
    private byte[] responseBody;

    public HttpAction(CloseableHttpClient closeableHttpClient, String url) {
        this.closeableHttpClient = closeableHttpClient;
        this.url = url;
        addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    }

    protected abstract HttpUriRequestBase httpUriRequest();

    public H doAction() {
        try {
            HttpUriRequestBase request = httpUriRequest();
            for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
            responseBody = closeableHttpClient.execute(request, response -> {
                HttpAction.this.response = response;
                return EntityUtils.toByteArray(response.getEntity());
            });
            return (H) this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String bodyString() {
        return new String(responseBody, StandardCharsets.UTF_8);
    }

    public JSONObject bodyJson() {
        return FastJsonUtil.parse(bodyString());
    }

    public H addRequestHeader(String headerKey, String headerValue) {
        this.requestHeaders.put(headerKey, headerValue);
        return (H) this;
    }

    public H addRequestParams(Map<String, Object> params) {
        return addRequestParams(params, true);
    }

    public H addRequestParams(Map<String, Object> params, boolean urlEncode) {
        for (Map.Entry<String, Object> stringObjectEntry : params.entrySet()) {
            addRequestParam(stringObjectEntry.getKey(), stringObjectEntry.getValue(), urlEncode);
        }
        return (H) this;
    }

    public H addRequestParam(String key, Object value) {
        return addRequestParam(key, value, true);
    }

    public H addRequestParam(String key, Object value, boolean urlEncode) {
        if (urlEncode) {
            value = URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8);
        }
        addRequestParam0(key, value);
        return (H) this;
    }

    protected abstract void addRequestParam0(String key, Object value);

}
