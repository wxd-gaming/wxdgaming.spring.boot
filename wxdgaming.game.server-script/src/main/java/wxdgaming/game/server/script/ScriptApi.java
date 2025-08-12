package wxdgaming.game.server.script;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.executor.ThreadContext;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.boot.net.ann.RpcRequest;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-14 19:22
 **/
@Slf4j
@Component
@RequestMapping(path = "/script")
public class ScriptApi extends HoldRunApplication {


    @RequestMapping(path = "/stop")
    public RunResult stop() {
        Thread.ofPlatform().start(() -> {
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
            System.exit(1);
        });
        return RunResult.ok();
    }

    @RpcRequest
    public JSONObject rpcIndex(@RequestParam("a") String a, JSONObject paramData) {
        log.info("{} {} {}", a, paramData, ThreadContext.context().queueName());
        return paramData;
    }

    @RpcRequest
    public JSONObject rpcIndex2(JSONObject paramData) {
        log.info("{} {}", paramData, ThreadContext.context().queueName());
        return paramData;
    }

    // @Scheduled("*/30")
    // public void timer() {
    //     log.info("{}", "timer()");
    // }
    //
    // @Scheduled("*/30")
    // @ExecutorWith(useVirtualThread = true)
    // public void timerAsync() {
    //     log.info("{}", "timerAsync()");
    // }

}
