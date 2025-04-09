package wxdgaming.spring.boot.starter.net.module.inner;

import wxdgaming.spring.boot.starter.core.assist.JavassistProxy;
import wxdgaming.spring.boot.starter.net.ann.RpcRequest;

/**
 * rpc 映射
 *
 * @param rpcRequest
 * @param path
 * @param proxy
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-04-09 16:34
 */
public record RpcMapping(RpcRequest rpcRequest, String path, JavassistProxy proxy) {

}
