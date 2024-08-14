package wxdgaming.spring.boot.webclient;

import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.threading.VirtualExecutor;

import java.io.File;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * http client 处理器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-12 17:07
 **/
@Slf4j
@Service
public class HttpClientService {

    final Executor executor;
    final CloseableHttpClient closeableHttpClient;

    @Autowired
    public HttpClientService(VirtualExecutor virtualExecutor, CloseableHttpClient closeableHttpClient) {
        this.executor = virtualExecutor;
        this.closeableHttpClient = closeableHttpClient;
    }

    @Scheduled(cron = "0 0 0/5 * * ?")
    public void te() {

    }

    public HttpGetWork doGet(String url) {
        return new HttpGetWork(executor, closeableHttpClient, url);
    }

    public HttpPostTextWork doPostText(String url) {
        return new HttpPostTextWork(executor, closeableHttpClient, url);
    }

    public HttpPostTextWork doPostText(String url, String data) {
        return doPostText(url).addRequestParam(data);
    }

    public HttpPostTextWork doPostText(String url, Map<String, Object> datas) {
        return doPostText(url).addRequestParams(datas);
    }

    public HttpPostJsonWork doPostJson(String url, String json) {
        return new HttpPostJsonWork(executor, closeableHttpClient, url).setJson(json);
    }

    public HttpPostJsonWork doPostJson(String url, Map<String, Object> datas) {
        return doPostJson(url, FastJsonUtil.toJsonKeyAsString(datas));
    }

    /** 多段式提交 */
    public HttpPostMultiWork doPostMulti(String url) {
        return new HttpPostMultiWork(executor, closeableHttpClient, url);
    }

    /** 多段式提交 */
    public HttpPostMultiWork doPostMulti(String url, Map<String, Object> datas) {
        return doPostMulti(url).addRequestParams(datas);
    }

    /** 上传文件 */
    public HttpPostFileWork doPostFile(String url) {
        return new HttpPostFileWork(executor, closeableHttpClient, url);
    }

    /** 上传文件 */
    public HttpPostFileWork doPostFile(String url, File file) {
        return doPostFile(url).addFile(file);
    }


}
