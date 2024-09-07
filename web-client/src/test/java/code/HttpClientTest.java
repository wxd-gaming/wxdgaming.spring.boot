package code;

import org.junit.Before;
import org.junit.Test;
import reactor.core.publisher.Mono;
import wxdgaming.spring.boot.core.threading.ExecutorBuilder;
import wxdgaming.spring.boot.webclient.HttpClientConfig;
import wxdgaming.spring.boot.webclient.HttpClientService;
import wxdgaming.spring.boot.webclient.HttpGetWork;

/**
 * http client test
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:53
 **/
public class HttpClientTest {

    HttpClientService httpClientService;

    @Before
    public void s() {
        HttpClientConfig httpClientConfig = new HttpClientConfig();
        ExecutorBuilder executorBuilder = new ExecutorBuilder();
        httpClientService = new HttpClientService(executorBuilder.virtualExecutor(), httpClientConfig.httpClient());
    }

    @Test
    public void h0() {
        String string = httpClientService.doGet("https://www.baidu.com").request().bodyString();
        System.out.println(string);
    }

    @Test
    public void h1() throws InterruptedException {
        Mono<HttpGetWork> httpGetActionMono = httpClientService.doGet("https://www.baidu.com").requestAsync();
        httpGetActionMono.subscribe(
                httpGetAction -> {
                    System.out.println(httpGetAction.bodyString());
                    System.out.println(Thread.currentThread().getName());
                },
                throwable -> {throwable.printStackTrace();}
        );
        httpGetActionMono.block();
    }

}
