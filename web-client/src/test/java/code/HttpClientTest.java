package code;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.junit.Before;
import org.junit.Test;
import wxdgaming.spring.boot.webclient.HttpClientConfig;
import wxdgaming.spring.boot.webclient.HttpClientService;

/**
 * http client test
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:53
 **/
public class HttpClientTest {

    CloseableHttpClient closeableHttpClient;
    HttpClientService httpClientService;

    @Before
    public void s() {
        HttpClientConfig httpClientConfig = new HttpClientConfig();
        closeableHttpClient = httpClientConfig.httpClient();
        httpClientService = new HttpClientService(null, null, closeableHttpClient);
    }

    @Test
    public void h0() {

    }

}
