package wxdgaming.spring.boot.webclient;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import reactor.core.publisher.Mono;
import wxdgaming.spring.boot.core.json.FastJsonUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * http 执行器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 17:33
 **/
@Slf4j
@Getter
public abstract class HttpWork {

    private final Executor executor;
    private final CloseableHttpClient closeableHttpClient;
    private final String url;
    protected ContentType contentType = ContentType.APPLICATION_FORM_URLENCODED;
    private final Map<String, String> requestHeaders = new LinkedHashMap<>();
    private ClassicHttpResponse response;
    private byte[] responseBody;

    public HttpWork(Executor executor, CloseableHttpClient closeableHttpClient, String url) {
        this.executor = executor;
        this.closeableHttpClient = closeableHttpClient;
        this.url = url;
        addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
    }

    protected abstract HttpUriRequestBase httpUriRequest();

    /** 请求 */
    public HttpWork request() {
        try {
            HttpUriRequestBase request = httpUriRequest();
            for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                request.setHeader(entry.getKey(), entry.getValue());
            }
            request.setHeader("Content-Type", contentType.toString());
            responseBody = closeableHttpClient.execute(request, response -> {
                HttpWork.this.response = response;
                return EntityUtils.toByteArray(response.getEntity());
            });
            return this;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends HttpWork, M extends Mono<T>> M requestAsync() {
        final CompletableFuture<T> future = new CompletableFuture<>();
        this.executor.execute(() -> {
            try {
                future.complete((T) request());
            } catch (Throwable e) {
                future.completeExceptionally(e);
            }
        });
        return (M) Mono.fromFuture(future);
    }

    /**
     * 添加 head 标记
     *
     * @param headerKey   key
     * @param headerValue value
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-13 21:07
     */
    public HttpWork addRequestHeader(String headerKey, String headerValue) {
        this.requestHeaders.put(headerKey, headerValue);
        return this;
    }

    public String bodyString() {
        return new String(responseBody, StandardCharsets.UTF_8);
    }

    public JSONObject bodyJson() {
        return FastJsonUtil.parse(bodyString());
    }

}
