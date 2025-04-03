package wxdgaming.spring.boot.starter.net.httpclient;


import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.apache.hc.core5.http.ContentType;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.starter.core.ann.AppStart;
import wxdgaming.spring.boot.starter.core.cache2.CASCache;
import wxdgaming.spring.boot.starter.core.cache2.Cache;
import wxdgaming.spring.boot.starter.core.function.Function1;
import wxdgaming.spring.boot.starter.core.threading.ExecutorUtil;
import wxdgaming.spring.boot.starter.core.threading.ExecutorUtilImpl;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-07-26 15:37
 **/
@Setter
@Service
@ConfigurationProperties(prefix = "http")
public class HttpClientBuilder {

    protected HttpClientConfig client;
    protected Cache<String, HttpClientPool> HTTP_CLIENT_CACHE;

    @AppStart
    public void init() {
        if (client == null) {
            client = HttpClientPoolScan.DEFAULT;
        }
        HTTP_CLIENT_CACHE = CASCache.<String, HttpClientPool>builder()
                .cacheName("http-client")
                .expireAfterWriteMs(TimeUnit.MINUTES.toMillis(client.getResetTimeM()))
                .loader((Function1<String, HttpClientPool>) s -> new HttpClientPool(client))
                .removalListener((k, pool) -> {
                    ExecutorUtilImpl.getInstance().getBasicExecutor().schedule(
                            pool::shutdown,
                            30,
                            TimeUnit.SECONDS
                    );
                    return true;
                })
                .build();
        HTTP_CLIENT_CACHE.start();
    }

    protected HttpClientPool httpClientPool() {
        return HTTP_CLIENT_CACHE.get("default");
    }

    public Get get(String uriPath) {
        return get(httpClientPool(), uriPath);
    }

    public Get get(HttpClientPool httpClientPool, String uriPath) {
        return new Get(httpClientPool, uriPath);
    }

    public PostMulti postMulti(String uriPath) {
        return postMulti(httpClientPool(), uriPath);
    }

    public PostMulti postMulti(HttpClientPool httpClientPool, String uriPath) {
        return new PostMulti(httpClientPool, uriPath);
    }

    public PostMultiFile postMultiFile(String uriPath) {
        return postMultiFile(httpClientPool(), uriPath);
    }

    public PostMultiFile postMultiFile(String uriPath, File file) {
        return postMultiFile(httpClientPool(), uriPath).addFile(file);
    }

    public PostMultiFile postMultiFile(HttpClientPool httpClientPool, String uriPath) {
        return new PostMultiFile(httpClientPool, uriPath);
    }

    public PostText postText(String uriPath) {
        return postText(httpClientPool(), uriPath);
    }

    public PostText postText(String uriPath, String param) {
        return postText(httpClientPool(), uriPath).setParams(param);
    }

    public PostText postText(HttpClientPool httpClientPool, String uriPath) {
        return new PostText(httpClientPool, uriPath);
    }

    public PostText postJson(String uriPath, String json) {
        return postJson(httpClientPool(), uriPath).setParamsJson(json);
    }

    public PostText postJson(HttpClientPool httpClientPool, String uriPath) {
        return new PostText(httpClientPool, uriPath).setContentType(ContentType.APPLICATION_JSON);
    }

}
