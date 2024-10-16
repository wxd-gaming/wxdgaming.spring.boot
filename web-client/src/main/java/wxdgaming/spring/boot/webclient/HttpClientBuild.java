package wxdgaming.spring.boot.webclient;

import lombok.Getter;
import lombok.Setter;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

/**
 * 初始化
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 17:09
 **/
@Getter
@Setter
@Configuration
public class HttpClientBuild {

    private int maxTotal = 200;
    private int defaultMaxPerRoute = 100;
    private long connectionRequestTimeout = 2;
    private long responseTimeout = 2;
    private int soTimeout = 2;
    private int keepAliveTimeout = 15;
    private int evictIdleConnectionTimeout = 15;

    @Bean
    public CloseableHttpClient httpClient() {
        try {
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {return null;}

                public void checkClientTrusted(X509Certificate[] xcs, String str) {}

                public void checkServerTrusted(X509Certificate[] xcs, String str) {}
            };

            SSLContext sslContext = SSLContext.getInstance("tls");
            sslContext.init(null, new TrustManager[]{tm}, null);

            SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, (s, sslSession) -> true);


            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.getSocketFactory())
                    .register("https", sslSocketFactory)
                    .build();

            // 初始化http连接池
            PoolingHttpClientConnectionManager connPoolMng = new PoolingHttpClientConnectionManager(registry);
            connPoolMng.setMaxTotal(maxTotal);
            connPoolMng.setDefaultMaxPerRoute(defaultMaxPerRoute);

            ConnectionConfig connectionConfig = ConnectionConfig.custom()
                    .setConnectTimeout(connectionRequestTimeout, TimeUnit.SECONDS)
                    .setSocketTimeout(soTimeout, TimeUnit.SECONDS)
                    .build();

            connPoolMng.setDefaultConnectionConfig(connectionConfig);

            // 初始化请求超时控制参数
            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout, TimeUnit.SECONDS) // 从线程池中获取线程超时时间
                    .setResponseTimeout(responseTimeout, TimeUnit.SECONDS) // 连接超时时间
                    .build();


            ConnectionKeepAliveStrategy connectionKeepAliveStrategy = (httpResponse, httpContext) -> {
                return TimeValue.of(keepAliveTimeout, TimeUnit.SECONDS); /*tomcat默认 keepAliveTimeout 为20s*/
            };

            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create()
                    .setConnectionManager(connPoolMng)
                    .evictExpiredConnections()/*关闭异常链接*/
                    .evictIdleConnections(TimeValue.of(evictIdleConnectionTimeout, TimeUnit.SECONDS))/*关闭空闲链接*/
                    .setDefaultRequestConfig(requestConfig)
                    .setRetryStrategy(new DefaultHttpRequestRetryStrategy())
                    .setKeepAliveStrategy(connectionKeepAliveStrategy);

            return httpClientBuilder.build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
