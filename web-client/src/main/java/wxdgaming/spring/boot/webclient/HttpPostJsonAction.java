package wxdgaming.spring.boot.webclient;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;

/**
 * http post json
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:21
 */
public class HttpPostJsonAction extends HttpAction<HttpPostJsonAction> {

    public HttpPostJsonAction(CloseableHttpClient closeableHttpClient, String url) {
        super(closeableHttpClient, url);
        contentType = ContentType.APPLICATION_JSON;
    }


}
