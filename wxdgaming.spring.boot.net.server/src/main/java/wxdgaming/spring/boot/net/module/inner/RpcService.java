package wxdgaming.spring.boot.net.module.inner;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import wxdgaming.spring.boot.core.CoreConfiguration;
import wxdgaming.spring.boot.core.cache2.CASCache;
import wxdgaming.spring.boot.core.cache2.Cache;
import wxdgaming.spring.boot.core.format.HexId;
import wxdgaming.spring.boot.core.util.Md5Util;
import wxdgaming.spring.boot.core.zip.GzipUtil;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.module.inner.message.ReqRemote;
import wxdgaming.spring.boot.net.module.inner.message.ResRemote;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * rpc 服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-02-17 19:25
 **/
@Slf4j
@Getter
@ComponentScan(basePackageClasses = {CoreConfiguration.class})
@AutoConfigureAfter(CoreConfiguration.class)
@Component
public class RpcService {

    final HexId hexId;
    final RpcListenerFactory rpcListenerFactory;
    final Cache<Long, CompletableFuture<JSONObject>> rpcCache;

    String rpcToken = null;

    @Autowired
    public RpcService(@Value("${sid}") int sid, @Value("${rpc.token}") String rpcToken, RpcListenerFactory rpcListenerFactory) {
        this.hexId = new HexId(sid);
        this.rpcToken = rpcToken;
        this.rpcListenerFactory = rpcListenerFactory;
        this.rpcCache = CASCache.<Long, CompletableFuture<JSONObject>>builder()
                .cacheName("rpc-server")
                .heartTimeMs(TimeUnit.SECONDS.toMillis(1))
                .expireAfterWriteMs(TimeUnit.SECONDS.toMillis(60))
                .removalListener((key, value) -> {
                    log.debug("rpcCache remove key:{}", key);
                    value.completeExceptionally(new RuntimeException("time out"));
                    return true;
                })
                .build();
        this.rpcCache.start();
    }

    public String sign(long rpcId) {
        return Md5Util.md5DigestEncode0("#", String.valueOf(rpcId), rpcToken);
    }

    public CompletableFuture<JSONObject> responseFuture(long rpcId) {
        return rpcCache.getIfPresent(rpcId);
    }

    public Mono<JSONObject> request(SocketSession socketSession, String cmd, Object params) {
        return request(socketSession, cmd, params, !socketSession.isEnabledScheduledFlush());
    }

    public Mono<JSONObject> request(SocketSession socketSession, String cmd, Object params, boolean immediate) {
        return request(socketSession, cmd, JSONObject.toJSONString(params), immediate);
    }

    public Mono<JSONObject> request(SocketSession socketSession, String cmd, String params) {
        return request(socketSession, cmd, params, !socketSession.isEnabledScheduledFlush());
    }

    public Mono<JSONObject> request(SocketSession socketSession, String cmd, String params, boolean immediate) {
        ReqRemote reqRemote = new ReqRemote();
        long rpcId = hexId.newId();
        reqRemote
                .setUid(rpcId)
                .setToken(sign(rpcId))
                .setCmd(cmd)
                .setParams(params);
        if (reqRemote.getParams().length() > 1024) {
            reqRemote.setGzip(1);
            reqRemote.setParams(GzipUtil.gzip2String(reqRemote.getParams()));
        }
        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();
        Mono<JSONObject> jsonObjectMono = Mono.fromCompletionStage(completableFuture);
        rpcCache.put(reqRemote.getUid(), completableFuture);
        if (immediate)
            socketSession.writeAndFlush(reqRemote);
        else
            socketSession.write(reqRemote);
        return jsonObjectMono;
    }

    public void response(SocketSession socketSession, long rpcId, Object data) {
        response(socketSession, rpcId, String.valueOf(data), !socketSession.isEnabledScheduledFlush());
    }

    public void response(SocketSession socketSession, long rpcId, Object data, boolean immediate) {
        response(socketSession, rpcId, String.valueOf(data), immediate);
    }

    public void response(SocketSession socketSession, long rpcId, String data, boolean immediate) {
        ResRemote resRemote = new ResRemote();
        resRemote
                .setUid(rpcId)
                .setToken(sign(rpcId))
                .setParams(String.valueOf(data));

        if (resRemote.getParams().length() > 1024) {
            resRemote.setGzip(1);
            resRemote.setParams(GzipUtil.gzip2String(resRemote.getParams()));
        }
        if (immediate)
            socketSession.writeAndFlush(resRemote);
        else
            socketSession.write(resRemote);
    }

}
