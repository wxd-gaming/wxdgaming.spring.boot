package wxdgaming.spring.boot.rpc;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;
import wxdgaming.spring.boot.core.SpringUtil;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.json.FastJsonUtil;
import wxdgaming.spring.boot.core.util.StringsUtil;
import wxdgaming.spring.boot.net.MsgMapper;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.rpc.pojo.RpcMessage;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

/**
 * rpc 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-16 23:25
 **/
@Slf4j
@Service
public class RpcService implements InitPrint {

    private final SpringUtil springUtil;
    private final ConcurrentHashMap<String, RpcHandler> rpcHandlerMap = new ConcurrentHashMap<>();

    @Autowired
    public RpcService(SpringUtil springUtil) {
        this.springUtil = springUtil;
    }

    @Start
    @Order(99999)
    public void start() {
        springUtil.withMethodAnnotated(RPC.class)
                .forEach(method -> {
                    method.setAccessible(true);
                    Object bean = springUtil.getBean(method.getDeclaringClass());
                    String value = method.getAnnotation(RPC.class).value();
                    if (StringsUtil.emptyOrNull(value)) {
                        value = method.getName();
                    }
                    if (rpcHandlerMap.putIfAbsent(value, new RpcHandler(method, bean)) != null) {

                    }
                });
    }

    @MsgMapper
    public void rpcReqSocketAction(SocketSession session, RpcMessage.ReqRemote reqRemote) throws Exception {
        long rpcId = reqRemote.getRpcId();
        String rpcToken = reqRemote.getRpcToken();
        String cmd = reqRemote.getCmd();
        int gzip = reqRemote.getGzip();
        JSONObject params = FastJsonUtil.parse(reqRemote.getParams());
        RpcHandler rpcHandler = rpcHandlerMap.get(cmd);
        if (rpcHandler == null) {
            log.error("rpcHandler is null, cmd={}", cmd);
            return;
        }
        rpcHandler.getMethod().invoke(rpcHandler.getBean(), params);
    }

    @MsgMapper
    public void rpcResSocketAction(SocketSession session, RpcMessage.ResRemote resRemote) throws Exception {
        long rpcId = resRemote.getRpcId();
        String rpcToken = resRemote.getRpcToken();
        int gzip = resRemote.getGzip();
        JSONObject params = FastJsonUtil.parse(resRemote.getParams());

    }

    @Data
    public class RpcHandler {

        Object bean;
        Method method;

        public RpcHandler(Method method, Object bean) {
            this.method = method;
            this.bean = bean;
        }

        public void doAction(JSONObject jsonObject) {
            String path = jsonObject.getString("path");
            RpcHandler rpcHandler = RpcService.this.rpcHandlerMap.get(path);
            if (rpcHandler == null) {
                return;
            }

        }

    }

}
