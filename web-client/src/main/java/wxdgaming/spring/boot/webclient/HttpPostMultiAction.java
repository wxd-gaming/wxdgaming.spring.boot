package wxdgaming.spring.boot.webclient;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * http get
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:21
 */
@Getter
@Setter
@Accessors(chain = true)
public class HttpPostMultiAction<H extends HttpPostMultiAction> extends HttpAction<H> {

    private final Map<String, Object> paramMap = new LinkedHashMap<>();

    public HttpPostMultiAction(CloseableHttpClient closeableHttpClient, String url) {
        super(closeableHttpClient, url);
        contentType = ContentType.MULTIPART_FORM_DATA;
    }

    @Override protected HttpUriRequestBase httpUriRequest() {
        HttpPost httpPost = new HttpPost(getUrl());
        if (getParamMap() != null && !getParamMap().isEmpty()) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setContentType(getContentType());
            for (Map.Entry<String, Object> objectObjectEntry : getParamMap().entrySet()) {
                String key = String.valueOf(objectObjectEntry.getKey());
                if (objectObjectEntry.getValue() instanceof File file) {
                    builder.addBinaryBody(key, file);
                    builder.addTextBody(file.getName() + "_lastModified", file.lastModified() + "");
                } else if (objectObjectEntry.getValue() instanceof byte[] bytes) {
                    builder.addBinaryBody(key, bytes);
                } else {
                    builder.addTextBody(key, String.valueOf(objectObjectEntry.getValue()));
                }
            }
            HttpEntity build = builder.build();
            httpPost.setEntity(build);
        }
        return httpPost;
    }

    @Override protected void addRequestParam0(String key, Object value) {
        paramMap.put(key, value);
    }

}
