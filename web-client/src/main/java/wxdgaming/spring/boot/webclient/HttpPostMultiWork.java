package wxdgaming.spring.boot.webclient;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executor;


/**
 * http get
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:21
 */
@Getter
@Setter
@Accessors(chain = true)
public class HttpPostMultiWork extends HttpWork {

    private final Map<String, Object> paramMap = new LinkedHashMap<>();

    public HttpPostMultiWork(Executor executor, CloseableHttpClient closeableHttpClient, String url) {
        super(executor, closeableHttpClient, url);
        contentType = ContentType.MULTIPART_FORM_DATA;
    }

    @Override protected HttpUriRequestBase httpUriRequest() {
        HttpPost httpPost = new HttpPost(getUrl());
        if (getParamMap() != null && !getParamMap().isEmpty()) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setContentType(getContentType());
            for (Map.Entry<String, Object> objectObjectEntry : getParamMap().entrySet()) {
                String key = String.valueOf(objectObjectEntry.getKey());
                if (objectObjectEntry.getValue() instanceof File file) {
                    builder.addBinaryBody(key, file);
                    builder.addTextBody(file.getName() + "_lastModified", file.lastModified() + "");
                } else if (objectObjectEntry.getValue() instanceof byte[] bytes) {
                    builder.addBinaryBody(key, bytes);
                } else {
                    builder.addTextBody(key, String.valueOf(objectObjectEntry.getValue()));
                }
            }
            HttpEntity build = builder.build();
            httpPost.setEntity(build);
        }
        return httpPost;
    }

    /** 请求 */
    @Override public HttpPostMultiWork request() {
        super.request();
        return this;
    }

    @Override public HttpPostMultiWork addRequestHeader(String headerKey, String headerValue) {
        super.addRequestHeader(headerKey, headerValue);
        return this;
    }

    /** 添加参数 */
    public HttpPostMultiWork addRequestParams(Map<String, Object> params) {
        return addRequestParams(params, true);
    }

    /** 添加参数 */
    public HttpPostMultiWork addRequestParams(Map<String, Object> params, boolean urlEncode) {
        for (Map.Entry<String, Object> stringObjectEntry : params.entrySet()) {
            addRequestParam(stringObjectEntry.getKey(), stringObjectEntry.getValue(), urlEncode);
        }
        return this;
    }

    /** 添加参数 */
    public HttpPostMultiWork addRequestParam(String key, Object value) {
        return addRequestParam(key, value, true);
    }

    /** 添加参数 */
    public HttpPostMultiWork addRequestParam(String key, Object value, boolean urlEncode) {
        if (urlEncode) {
            value = URLEncoder.encode(String.valueOf(value), StandardCharsets.UTF_8);
        }
        addRequestParam0(key, value);
        return this;
    }

    /** 添加参数 */
    protected void addRequestParam0(String key, Object value) {
        paramMap.put(key, value);
    }

    @Override public HttpPostMultiWork connectTimeOut(long connectTimeOut) {
        super.connectTimeOut(connectTimeOut);
        return this;
    }

    @Override public HttpPostMultiWork responseTimeout(long responseTimeout) {
        super.responseTimeout(responseTimeout);
        return this;
    }

}
