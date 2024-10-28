package wxdgaming.spring.boot.webclient;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * http post file
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 20:21
 */
public class HttpPostFileWork extends HttpPostMultiWork {

    public HttpPostFileWork(Executor executor, CloseableHttpClient closeableHttpClient, String url) {
        super(executor, closeableHttpClient, url);
    }

    @Override public HttpPostFileWork addRequestHeader(String headerKey, String headerValue) {
        super.addRequestHeader(headerKey, headerValue);
        return this;
    }

    @Override public HttpPostFileWork addRequestParams(Map<String, Object> params) {
        super.addRequestParams(params);
        return this;
    }

    @Override public HttpPostFileWork addRequestParams(Map<String, Object> params, boolean urlEncode) {
        super.addRequestParams(params, urlEncode);
        return this;
    }

    @Override public HttpPostFileWork addRequestParam(String key, Object value) {
        super.addRequestParam(key, value);
        return this;
    }

    @Override public HttpPostFileWork addRequestParam(String key, Object value, boolean urlEncode) {
        super.addRequestParam(key, value, urlEncode);
        return this;
    }

    @Override protected void addRequestParam0(String key, Object value) {
        super.addRequestParam0(key, value);
    }

    @Override public HttpPostFileWork request() {
        super.request();
        return this;
    }

    public HttpPostFileWork addFile(String filePath) {
        File file1 = new File(filePath);
        this.getParamMap().put(file1.getName(), file1);
        return this;
    }

    public HttpPostFileWork addFile(File file) {
        this.getParamMap().put(file.getName(), file);
        return this;
    }

    @Override public HttpPostFileWork connectTimeOut(long connectTimeOut) {
        super.connectTimeOut(connectTimeOut);
        return this;
    }

    @Override public HttpPostFileWork responseTimeout(long responseTimeout) {
        super.responseTimeout(responseTimeout);
        return this;
    }
}
