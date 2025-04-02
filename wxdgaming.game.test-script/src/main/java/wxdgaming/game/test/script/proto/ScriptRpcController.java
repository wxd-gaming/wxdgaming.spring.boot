package wxdgaming.game.test.script.proto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import wxdgaming.spring.boot.starter.core.InitPrint;
import wxdgaming.spring.boot.starter.core.threading.ExecutorQueue;
import wxdgaming.spring.boot.starter.core.threading.ThreadContext;
import wxdgaming.spring.boot.starter.net.ann.RpcRequest;

/**
 * 测试通信
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-02 11:13
 **/
@Slf4j
@Component
@RequestMapping(path = "script")
public class ScriptRpcController implements InitPrint {

    @RpcRequest
    public void index() {
        ExecutorQueue queue = ThreadContext.context().queue();
        log.info("script/index - " + ThreadContext.context().queueName());
    }

}
