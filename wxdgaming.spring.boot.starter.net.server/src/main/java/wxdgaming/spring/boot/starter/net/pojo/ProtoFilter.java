package wxdgaming.spring.boot.starter.net.pojo;

import wxdgaming.spring.boot.starter.net.SocketSession;

/**
 * http 请求接口过滤器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-13 16:32
 **/
public interface ProtoFilter {

    boolean doFilter(ProtoListenerTrigger protoListenerTrigger,
                     SocketSession socketSession,
                     PojoBase pojoBase);

}
