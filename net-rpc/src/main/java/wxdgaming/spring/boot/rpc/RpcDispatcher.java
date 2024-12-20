package wxdgaming.spring.boot.rpc;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.SpringReflectContent;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.rpc.pojo.RpcMessage;

import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * rpc 派发
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-12-20 15:25
 **/
@Slf4j
@Getter
public class RpcDispatcher implements InitPrint {

    String RPC_TOKEN;
    private final ConcurrentHashMap<String, RpcActionMapping> rpcHandlerMap = new ConcurrentHashMap<>();
    private final AtomicLong atomicLong = new AtomicLong(0);
    private final ConcurrentSkipListMap<Long, CompletableFuture<String>> rpcEvent = new ConcurrentSkipListMap<>();

    public RpcDispatcher(String RPC_TOKEN) {
        this.RPC_TOKEN = RPC_TOKEN;
    }

    public void initMapping(SpringReflectContent springReflectContent) {
        springReflectContent.withMethodAnnotated(RPC.class).forEach(t -> {
            t.getRight().setAccessible(true);
            String value = "";
            RequestMapping requestMapping = t.getLeft().getClass().getAnnotation(RequestMapping.class);
            if (requestMapping != null && requestMapping.value().length > 0) {
                value = requestMapping.value()[0];
            }
            RPC annotation = t.getRight().getAnnotation(RPC.class);
            String mapping = annotation.value();
            if (StringsUtil.emptyOrNull(mapping)) {
                value += "/" + t.getRight().getName();
            } else {
                value += mapping;
            }
            RpcActionMapping oldMapping = rpcHandlerMap.put(value, new RpcActionMapping(annotation, t.getLeft(), t.getRight()));
            if (oldMapping != null) {
                if (!oldMapping.getBean().getClass().getName().endsWith(t.getLeft().getClass().getName())) {
                    throw new RuntimeException("RPC 处理器重复：" + oldMapping.getBean().getClass().getName() + " - " + t.getLeft().getClass().getName());
                }
            }
            log.debug("rpc register path={}, {}#{}", value, t.getLeft().getClass().getName(), t.getRight().getName());
        });
    }

    public Object rpcReqSocketAction(SocketSession session, String rpcToken, long rpcId, long targetId, String path, String remoteParams) throws Exception {
        RpcActionMapping rpcActionMapping = getRpcHandlerMap().get(path);
        if (rpcActionMapping == null) {
            log.error("rpc 调用异常 rpcId={}, path={}, params={}", rpcId, path, remoteParams);
            response(session, rpcId, targetId, 9, "Not found path=【" + path + "】!");
            return null;
        }
        if (rpcActionMapping.getAnnotation().checkToken()) {
            if (!getRPC_TOKEN().equalsIgnoreCase(rpcToken)) {
                log.error("rpc 调用验签失败 rpcId={}, path={}, params={}", rpcId, path, remoteParams);
                response(session, rpcId, targetId, 10, "token error path=【" + path + "】!");
                return null;
            }
        }
        Parameter[] parameters = rpcActionMapping.getMethod().getParameters();
        Object[] params = new Object[parameters.length];
        for (int i = 0; i < params.length; i++) {
            Parameter parameter = parameters[i];
            Type type = parameter.getParameterizedType();
            if (type instanceof Class<?> clazz) {
                if (clazz.isAssignableFrom(session.getClass())) {
                    params[i] = session;
                } else {
                    /*实现注入*/
                    RequestParam requestParam = parameter.getAnnotation(RequestParam.class);
                    if (requestParam != null) {
                        String name = requestParam.name();
                        if (StringsUtil.emptyOrNull(name)) {
                            name = requestParam.value();
                        }
                        if (StringsUtil.emptyOrNull(name)) {
                            throw new RuntimeException(rpcActionMapping.getBean().getClass().getName() + "#" + rpcActionMapping.getMethod().getName() + ", 无法识别 " + (i + 1) + " 参数 RequestParam 指定 name " + clazz);
                        }
                        params[i] = FastJsonUtil.parse(remoteParams).getObject(name, clazz);
                        continue;
                    }
                    if (clazz.isAssignableFrom(String.class)) {
                        params[i] = remoteParams;
                    } else if (clazz.isAssignableFrom(JSONObject.class)) {
                        params[i] = FastJsonUtil.parse(remoteParams);
                    } else {
                        throw new RuntimeException(rpcActionMapping.getBean().getClass().getName() + "#" + rpcActionMapping.getMethod().getName() + ", 无法识别 " + (i + 1) + " 参数 " + clazz);
                    }
                }
            }
        }

        /* 调用 */
        return rpcActionMapping.getMethod().invoke(rpcActionMapping.getBean(), params);
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
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-22 19:41
     */
    public Mono<String> request(SocketSession session, long targetId, String path, JSONObject params) {
        return request(session, targetId, path, FastJsonUtil.toJsonWriteType(params));
    }

    /**
     * 请求 rpc 执行
     *
     * @param session 链接
     * @param path    路径
     * @param params  参数
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-22 19:41
     */
    public Mono<String> request(SocketSession session, long targetId, String path, String params) {
        long rpcId = atomicLong.incrementAndGet();
        RpcMessage.ReqRemote rpcMessage = new RpcMessage.ReqRemote()
                .setRpcId(rpcId)
                .setTargetId(targetId)
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
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-08-22 19:41
     */
    public void response(SocketSession session, long rpcId, long targetId, int code, String params) {
        RpcMessage.ResRemote resRemote = new RpcMessage.ResRemote()
                .setRpcId(rpcId)
                .setTargetId(targetId)
                .setRpcToken(RPC_TOKEN)
                .setCode(code)
                .setParams(params);
        session.writeAndFlush(resRemote);
    }

}
