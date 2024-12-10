package wxdgaming.spring.boot.webclient;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.threading.VirtualExecutor;
import wxdgaming.spring.boot.core.timer.MyClock;

/**
 * 飞书服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-10 16:16
 **/
@Service
public class FeishuService {

    final HttpClientService httpClientService;
    final VirtualExecutor virtualExecutor;

    public FeishuService(HttpClientService httpClientService, VirtualExecutor virtualExecutor) {
        this.httpClientService = httpClientService;
        this.virtualExecutor = virtualExecutor;
    }

    /**
     * 发送飞书通知
     *
     * @param url     请求地址
     * @param key     关键字key
     * @param context 内容
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-12-10 16:24
     */
    public void sendFeiShu(String url, String key, String context) {
        String property = System.getProperty("user.dir");
        Thread thread = Thread.currentThread();
        virtualExecutor.execute(() -> {
            String finalContext = key
                                  + "\n运行目录：" + property
                                  + "\n时间：" + MyClock.nowString()
                                  + "\n线程：" + thread.getName()
                                  + "\n\n内容：\n" + context;

            JSONObject objMap = new JSONObject()
                    .fluentPut("msg_type", "text")
                    .fluentPut("content", new JSONObject().fluentPut("text", finalContext));
            String string = httpClientService.doPostJson(url, objMap)
                    .connectTimeOut(5000)
                    .responseTimeout(5000)
                    .request()
                    .bodyString();
            System.out.println("飞书 - " + string);
        });
    }

}
