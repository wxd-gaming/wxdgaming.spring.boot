package wxdgaming.spring.boot.starter.net.module.inner;

import com.alibaba.fastjson.JSONObject;
import wxdgaming.spring.boot.starter.net.SocketSession;

/**
 * http 请求接口过滤器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:32
 **/
public interface RpcFilter {

    boolean doFilter(RpcListenerTrigger rpcListenerTrigger,
                     String cmd,
                     SocketSession socketSession,
                     JSONObject paramObject);

}
