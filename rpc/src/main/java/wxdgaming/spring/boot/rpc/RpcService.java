package wxdgaming.spring.boot.rpc;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.rpc.pojo.RpcMessage;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * rpc 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-16 23:25
 **/
@Slf4j
@Getter
@Service
public class RpcService implements InitPrint {

    private final SpringUtil springUtil;
    private final ConcurrentHashMap<String, RpcActionMapping> rpcHandlerMap = new ConcurrentHashMap<>();
    private final String RPC_TOKEN = "getg6jhkopw435dvmkmcvx5y63-40";
    private final AtomicLong atomicLong = new AtomicLong(0);
    private final ConcurrentSkipListMap<Long, CompletableFuture<String>> rpcEvent = new ConcurrentSkipListMap<>();

    @Autowired
    public RpcService(SpringUtil springUtil) {
        this.springUtil = springUtil;
    }

    @Start
    @Order(99999)
    public void start() {
        springUtil.withMethodAnnotated(RPC.class)
                .forEach(t -> {
                    t.getRight().setAccessible(true);
                    String value = "";
                    RequestMapping requestMapping = t.getLeft().getAnnotation(RequestMapping.class);
                    if (requestMapping != null && requestMapping.value().length > 0) {
                        value = requestMapping.value()[0];
                    }
                    Object bean = springUtil.getBean(t.getLeft());
                    String mapping = t.getRight().getAnnotation(RPC.class).value();
                    if (StringsUtil.emptyOrNull(mapping)) {
                        value += "/" + t.getRight().getName();
                    } else {
                        value += mapping;
                    }
                    if (rpcHandlerMap.putIfAbsent(value, new RpcActionMapping(t.getRight(), bean)) != null) {

                    }
                    log.debug("rpc register path={}, {}#{}", value, t.getLeft().getName(), t.getRight().getName());
                });
    }

    /** rpc test */
    @RPC("rpcTest")
    public String rpcTest(SocketSession session, JSONObject jsonObject, @RequestParam(name = "type") int type) throws Exception {
        log.debug("rpc action rpcTest {}, {}, {}", session, jsonObject, type);
        return "ok";
    }

    /**
     * 请求 rpc 执行
     *
     * @param session 链接
     * @param path    路径
     * @param params  参数
     * @return
     * @throws Exception
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-22 19:41
     */
    public Mono<String> request(SocketSession session, String path, JSONObject params) {
        return request(session, path, FastJsonUtil.toJsonWriteType(params));
    }

    /**
     * 请求 rpc 执行
     *
     * @param session 链接
     * @param path    路径
     * @param params  参数
     * @return
     * @throws Exception
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-22 19:41
     */
    public Mono<String> request(SocketSession session, String path, String params) {
        long rpcId = atomicLong.incrementAndGet();
        RpcMessage.ReqRemote rpcMessage = new RpcMessage.ReqRemote()
                .setRpcId(rpcId)
                .setPath(path)
                .setRpcToken(RPC_TOKEN)
                .setParams(params);

        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        rpcEvent.put(rpcId, completableFuture);
        session.writeAndFlush(rpcMessage);
        return Mono.fromFuture(completableFuture);
    }

    /**
     * 回应 rpc 执行结果
     *
     * @param session 链接
     * @param rpcId   请求 id
     * @param code    执行状态
     * @param params  参数
     * @throws Exception 异常
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-22 19:41
     */
    public void response(SocketSession session, long rpcId, int code, String params) {
        RpcMessage.ResRemote resRemote = new RpcMessage.ResRemote()
                .setRpcId(rpcId)
                .setRpcToken(RPC_TOKEN)
                .setCode(code)
                .setParams(params);
        session.writeAndFlush(resRemote);
    }

}
