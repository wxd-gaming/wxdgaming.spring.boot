package wxdgaming.game.test.proto;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import wxdgaming.spring.boot.starter.core.ann.ThreadParam;
import wxdgaming.spring.boot.starter.core.system.AnnUtil;
import wxdgaming.spring.boot.starter.core.threading.ExecutorQueue;
import wxdgaming.spring.boot.starter.core.threading.ExecutorWith;
import wxdgaming.spring.boot.starter.core.threading.ThreadContext;
import wxdgaming.spring.boot.starter.net.SocketSession;
import wxdgaming.spring.boot.starter.net.ann.RpcRequest;
import wxdgaming.spring.boot.starter.net.module.inner.RpcFilter;
import wxdgaming.spring.boot.starter.net.module.inner.RpcListenerTrigger;

import java.lang.reflect.Method;

/**
 * 测试通信
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-02 11:13
 **/
@Slf4j
@Component
@RequestMapping(path = "test")
public class TestRpc implements RpcFilter {

    @RpcRequest
    @ExecutorWith(queueName = "guild")
    public void index(@ThreadParam(path = "queueName") String queueName, @Value("${sid:0}") int sid, @RequestParam("name") String name) {
        ExecutorQueue queue = ThreadContext.context().queue();
        log.info("test/index - " + sid + " - " + ThreadContext.context().queueName() + " - " + queueName);
    }

    @Override public boolean doFilter(RpcListenerTrigger rpcListenerTrigger, String cmd, SocketSession socketSession, JSONObject paramObject) {
        Method method = rpcListenerTrigger.getRpcMapping().method();
        AnnUtil.annOpt(method, ExecutorWith.class)
                .ifPresent(executorWith -> {
                    if ("guild".equals(executorWith.queueName())) {
                        long guild = 1;
                        rpcListenerTrigger.setQueueName("guild" + guild);
                    }
                });

        return true;
    }
}
