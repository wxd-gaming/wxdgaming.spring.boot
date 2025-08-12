package wxdgaming.game.server.api;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.executor.ThreadContext;
import wxdgaming.spring.boot.net.ann.RpcRequest;
import wxdgaming.spring.boot.scheduled.ann.Scheduled;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-14 19:22
 **/
@Slf4j
@Component
@RequestMapping(path = "/")
public class TestApi extends HoldRunApplication {


    public ConcurrentHashMap<String, String> strMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, String> str2Map = new ConcurrentHashMap<>();

    public TestApi() {
        strMap.put("a", "b");
        str2Map.put("a", "b");
    }

    @RpcRequest
    public JSONObject rpcIndex2(JSONObject paramData, @RequestParam("a") String a) {
        log.debug("{} {} {}", a, paramData, ThreadContext.context().queueName());
        return paramData;
    }

    @Scheduled("0 0")
    public void timer() {
        log.debug("{}", "timer()");
        runApplication.executorWithMethodAnnotated(RunTest.class, 1, 2);
    }

    @Documented
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @Target({java.lang.annotation.ElementType.METHOD})
    @interface RunTest {

    }

    @RunTest
    public void runTestParam(@Value("${sid}") int sid, int a, int b) {
        log.info("{} sid={}, a={}, b={}", "runTest()", sid, a, b);
    }

    // @Scheduled("*/30")
    // @ExecutorWith(useVirtualThread = true)
    // public void timerAsync() {
    //     log.info("{}", "timerAsync()");
    // }


    public void print() {
        log.info("{} {}", "print()", FastJsonUtil.toJSONString(strMap));
    }

}
