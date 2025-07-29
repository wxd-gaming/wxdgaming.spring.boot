package code;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.net.httpclient.HttpClientConfiguration;
import wxdgaming.spring.boot.net.httpclient.HttpRequestGet;
import wxdgaming.spring.boot.net.httpclient.HttpResponse;

@SpringBootApplication(
        scanBasePackageClasses = {
                HttpClientConfiguration.class,
        }
)
public class HttpClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpClientApplication.class, args);

        HttpResponse httpResponse = HttpRequestGet.of("https://www.baidu.com").execute();
        String string = httpResponse.bodyString();
        System.out.println(string);

    }

}
