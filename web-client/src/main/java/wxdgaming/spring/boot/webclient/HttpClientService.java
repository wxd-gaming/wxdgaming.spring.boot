package wxdgaming.spring.boot.webclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.threading.VirtualExecutor;

import java.util.concurrent.CompletableFuture;

/**
 * http client 处理器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 17:07
 **/
@Slf4j
@Service
public class HttpClientService {

    final SpringUtil springUtil;
    final VirtualExecutor virtualExecutor;
    final CloseableHttpClient closeableHttpClient;

    public HttpClientService(SpringUtil springUtil, VirtualExecutor virtualExecutor, CloseableHttpClient closeableHttpClient) {
        this.springUtil = springUtil;
        this.virtualExecutor = virtualExecutor;
        this.closeableHttpClient = closeableHttpClient;
    }

    public HttpGetAction doGet(String url) {
        return new HttpGetAction(closeableHttpClient, url);
    }

    public CompletableFuture<HttpGetAction> doGetSync(String url) {
        return virtualExecutor.submit(() -> {
            return new HttpGetAction(closeableHttpClient, url);
        });
    }


    public HttpPostTextAction doPost(String url) {
        return new HttpPostTextAction(closeableHttpClient, url);
    }


}
