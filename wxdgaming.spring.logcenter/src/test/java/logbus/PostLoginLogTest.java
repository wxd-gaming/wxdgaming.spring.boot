package logbus;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.boot.net.httpclient.HttpClientConfiguration;
import wxdgaming.spring.boot.net.httpclient.HttpRequestPost;
import wxdgaming.spring.boot.net.httpclient.HttpResponse;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-08-07 19:23
 **/
@SpringBootTest(
        classes = {
                CoreConfiguration.class,
                HttpClientConfiguration.class,
        }
)
public class PostLoginLogTest {


    @RepeatedTest(10)
    public void login() {
        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("uid", System.nanoTime());
        jsonObject.put("createTime", System.currentTimeMillis());
        jsonObject.put("logType", "login");
        jsonObject.put("json", MapOf.newJSONObject().fluentPut("account", "wxd-gaming"));
        HttpRequestPost httpRequestPost = HttpRequestPost.of("http://localhost:8888/api/log/push");
        httpRequestPost.setJson(jsonObject);
        HttpResponse execute = httpRequestPost.execute();
        System.out.println(execute.bodyString());
    }

    @RepeatedTest(10000)
    public void loginList() {
        ArrayList<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            JSONObject jsonObject = MapOf.newJSONObject();
            jsonObject.put("uid", System.nanoTime());
            jsonObject.put("createTime", System.currentTimeMillis());
            jsonObject.put("logType", "login");
            jsonObject.put("json", MapOf.newJSONObject().fluentPut("account", "wxd-gaming"));

            list.add(jsonObject);
        }
        HttpRequestPost httpRequestPost = HttpRequestPost.of("http://localhost:8888/api/log/pushlist");
        httpRequestPost.setJson(JSON.toJSONString(list));

        HttpResponse execute = httpRequestPost.execute();
        System.out.println(execute.bodyString());
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(33));
    }

    @RepeatedTest(10)
    public void logout() {
        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("uid", System.nanoTime());
        jsonObject.put("createTime", System.currentTimeMillis());
        jsonObject.put("logType", "logout");
        jsonObject.put("json", MapOf.newJSONObject().fluentPut("account", "wxd-gaming"));
        HttpRequestPost httpRequestPost = HttpRequestPost.of("http://localhost:8888/api/log/push");
        httpRequestPost.setJson(jsonObject);
        HttpResponse execute = httpRequestPost.execute();
        System.out.println(execute.bodyString());
    }

}
