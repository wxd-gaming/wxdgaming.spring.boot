package wxdgaming.spring.boot.net.httpclient;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.cache2.CASCache;
import wxdgaming.spring.boot.core.cache2.Cache;
import wxdgaming.spring.boot.core.executor.ExecutorFactory;
import wxdgaming.spring.boot.core.function.Function1;

import java.util.concurrent.TimeUnit;

/**
 * 扫描器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-07-21 19:54
 */
@Slf4j
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "http-client")
@EnableConfigurationProperties({HttpClientConfiguration.class})
public class HttpClientConfiguration implements InitPrint {

    @Getter private static HttpClientConfiguration instance = null;
    private Cache<String, HttpClientPool> HTTP_CLIENT_CACHE;

    private HttpClientConfig config;

    @Autowired
    public HttpClientConfiguration(CoreConfiguration coreConfiguration) {}

    @PostConstruct
    public void init() {
        if (config == null) {
            config = HttpClientConfig.DEFAULT.get();
        }
        log.debug("HttpClientConfig: {}", config);
        HTTP_CLIENT_CACHE = CASCache.<String, HttpClientPool>builder()
                .cacheName("http-client")
                .expireAfterWriteMs(TimeUnit.MINUTES.toMillis(config.getResetTimeM()))
                .loader((Function1<String, HttpClientPool>) s -> new HttpClientPool(config))
                .removalListener((k, pool) -> {
                    ExecutorFactory.getExecutorServiceLogic().schedule(
                            pool::shutdown,
                            30,
                            TimeUnit.SECONDS
                    );
                    return true;
                })
                .build();
        HTTP_CLIENT_CACHE.start();
        HttpClientConfiguration.instance = this;
    }

    public HttpClientPool getDefault() {
        return getDefault("0-0");
    }

    public HttpClientPool getDefault(String key) {
        return HTTP_CLIENT_CACHE.get(key);
    }

}
