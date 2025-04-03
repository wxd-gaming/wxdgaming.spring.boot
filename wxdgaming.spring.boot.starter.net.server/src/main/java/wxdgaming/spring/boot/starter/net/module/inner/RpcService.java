package wxdgaming.spring.boot.starter.net.module.inner;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.starter.core.ann.AppStart;
import wxdgaming.spring.boot.starter.core.cache2.CASCache;
import wxdgaming.spring.boot.starter.core.cache2.Cache;
import wxdgaming.spring.boot.starter.core.format.HexId;
import wxdgaming.spring.boot.starter.core.json.FastJsonUtil;
import wxdgaming.spring.boot.starter.core.zip.GzipUtil;
import wxdgaming.spring.boot.starter.net.SocketSession;
import wxdgaming.spring.boot.starter.net.module.inner.message.ReqRemote;
import wxdgaming.spring.boot.starter.net.module.inner.message.ResRemote;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * rpc 服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-02-17 19:25
 **/
@Slf4j
@Service
public class RpcService implements Closeable {

    final HexId hexId;
    final RpcListenerFactory rpcListenerFactory;

    final Cache<Long, CompletableFuture<JSONObject>> rpcCache;

    @Autowired
    public RpcService(@Value("${sid}") int sid, RpcListenerFactory rpcListenerFactory) {
        this.rpcListenerFactory = rpcListenerFactory;
        this.hexId = new HexId(sid);
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
    }

    @AppStart
    public void start() {
        this.rpcCache.start();
    }

    @Override public void close() throws IOException {
        this.rpcCache.shutdown();
        log.info("Rpc Service close");
    }

    public CompletableFuture<JSONObject> responseFuture(long rpcId) {
        return rpcCache.getIfPresent(rpcId);
    }

    public CompletableFuture<JSONObject> request(SocketSession socketSession, String cmd, Object params) {
        return request(socketSession, cmd, params, !socketSession.isEnabledScheduledFlush());
    }

    public CompletableFuture<JSONObject> request(SocketSession socketSession, String cmd, Object params, boolean immediate) {
        return request(socketSession, cmd, JSONObject.toJSONString(params), immediate);
    }

    public CompletableFuture<JSONObject> request(SocketSession socketSession, String cmd, String params) {
        return request(socketSession, cmd, params, !socketSession.isEnabledScheduledFlush());
    }

    public CompletableFuture<JSONObject> request(SocketSession socketSession, String cmd, String params, boolean immediate) {
        CompletableFuture<JSONObject> completableFuture = new CompletableFuture<>();
        ReqRemote reqRemote = new ReqRemote();
        reqRemote
                .setUid(hexId.newId())
                .setCmd(cmd)
                .setParams(params);

        if (reqRemote.getParams().length() > 1024) {
            reqRemote.setGzip(1);
            reqRemote.setParams(GzipUtil.gzip2String(reqRemote.getParams()));
        }
        rpcCache.put(reqRemote.getUid(), completableFuture);
        if (immediate)
            socketSession.writeAndFlush(reqRemote);
        else
            socketSession.write(reqRemote);
        return completableFuture;
    }

    public void response(SocketSession socketSession, long rpcId, Object data) {
        response(socketSession, rpcId, FastJsonUtil.toJSONString(data), !socketSession.isEnabledScheduledFlush());
    }

    public void response(SocketSession socketSession, long rpcId, Object data, boolean immediate) {
        response(socketSession, rpcId, FastJsonUtil.toJSONString(data), immediate);
    }

    public void response(SocketSession socketSession, long rpcId, String data, boolean immediate) {
        ResRemote resRemote = new ResRemote();
        resRemote
                .setUid(rpcId)
                .setToken("")
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
