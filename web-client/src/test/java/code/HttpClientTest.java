package code;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import wxdgaming.spring.boot.core.CoreScan;
import wxdgaming.spring.boot.webclient.HttpClientService;
import wxdgaming.spring.boot.webclient.HttpGetWork;
import wxdgaming.spring.boot.webclient.WebClientScan;

/**
 * http client test
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:53
 **/
@RunWith(SpringRunner.class)
@SpringBootApplication
@SpringBootTest(classes = {CoreScan.class, WebClientScan.class})
public class HttpClientTest {

    @Autowired HttpClientService httpClientService;


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

    @Test
    public void ip() {
        System.out.println(httpClientService.getCity4Ip("171.223.187.24"));
        System.out.println(httpClientService.getCity4Ip("42.102.207.241"));
        System.out.println(httpClientService.getCity4Ip("39.144.137.236"));
    }

}
