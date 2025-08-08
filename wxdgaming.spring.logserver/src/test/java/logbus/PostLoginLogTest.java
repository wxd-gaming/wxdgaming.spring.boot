package logbus;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.boot.test.context.SpringBootTest;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.chatset.StringUtils;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.boot.net.httpclient.HttpClientConfiguration;
import wxdgaming.spring.boot.net.httpclient.HttpRequestPost;
import wxdgaming.spring.boot.net.httpclient.HttpResponse;
import wxdgaming.spring.logserver.bean.LogEntity;

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

    static final HexId hexId = new HexId(1);

    @RepeatedTest(10)
    public void login() {
        LogEntity logEntity = new LogEntity();
        logEntity.setUid(hexId.newId());
        logEntity.setCreateTime(System.currentTimeMillis());
        logEntity.setLogType("login");
        logEntity.putLogData("account", StringUtils.randomString(8));
        HttpRequestPost httpRequestPost = HttpRequestPost.of("http://localhost:8888/api/log/push");
        httpRequestPost.setJson(logEntity.toJSONString());
        HttpResponse execute = httpRequestPost.execute();
        System.out.println(execute.bodyString());
    }

    @RepeatedTest(1000)
    public void loginList() {
        ArrayList<LogEntity> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            LogEntity logEntity = new LogEntity();
            logEntity.setUid(hexId.newId());
            logEntity.setCreateTime(System.currentTimeMillis());
            logEntity.setLogType("login");
            logEntity.putLogData("account", StringUtils.randomString(8));
            list.add(logEntity);
        }
        HttpRequestPost httpRequestPost = HttpRequestPost.of("http://localhost:8888/api/log/pushlist");
        httpRequestPost.setJson(JSON.toJSONString(list));

        HttpResponse execute = httpRequestPost.execute();
        System.out.println(execute.bodyString());
        LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(33));
    }

    @RepeatedTest(10)
    public void logout() {
        LogEntity logEntity = new LogEntity();
        logEntity.setUid(hexId.newId());
        logEntity.setCreateTime(System.currentTimeMillis());
        logEntity.setLogType("logout");
        logEntity.putLogData("account", StringUtils.randomString(8));
        HttpRequestPost httpRequestPost = HttpRequestPost.of("http://localhost:8888/api/log/push");
        httpRequestPost.setJson(logEntity.toJSONString());
        HttpResponse execute = httpRequestPost.execute();
        System.out.println(execute.bodyString());
    }

}
