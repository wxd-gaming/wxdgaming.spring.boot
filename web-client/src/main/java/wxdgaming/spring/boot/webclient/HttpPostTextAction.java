package wxdgaming.spring.boot.webclient;

import lombok.experimental.Accessors;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.io.entity.StringEntity;

import java.nio.charset.StandardCharsets;

/**
 * http post text
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:21
 */
@Accessors(chain = true)
public class HttpPostTextAction extends HttpAction<HttpPostTextAction> {

    private final StringBuilder stringBuilder = new StringBuilder();

    public HttpPostTextAction(CloseableHttpClient closeableHttpClient, String url) {
        super(closeableHttpClient, url);
    }

    @Override protected HttpUriRequestBase httpUriRequest() {
        HttpPost httpPost = new HttpPost(getUrl());
        httpPost.setEntity(new StringEntity(stringBuilder.toString(), StandardCharsets.UTF_8));
        return httpPost;
    }

    @Override protected void addRequestParam0(String key, Object value) {
        if (!stringBuilder.isEmpty()) {
            stringBuilder.append("&");
        }
        stringBuilder.append(key).append("=").append(value);
    }

    public void addRequestParam(String value) {
        if (!stringBuilder.isEmpty()) {
            stringBuilder.append("&");
        }
        stringBuilder.append(value);
    }

    public void setRequestParam(String value) {
        stringBuilder.setLength(0);
        stringBuilder.append(value);
    }

}
