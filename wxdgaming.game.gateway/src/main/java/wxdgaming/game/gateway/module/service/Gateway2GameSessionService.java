package wxdgaming.game.gateway.module.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Service;
import wxdgaming.game.gateway.GatewayBootstrapConfig;
import wxdgaming.game.login.bean.info.InnerServerInfoBean;
import wxdgaming.game.message.inner.InnerRegisterServer;
import wxdgaming.game.message.inner.ServiceType;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.Throw;
import wxdgaming.spring.boot.core.ann.Start;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.boot.core.executor.ExecutorWith;
import wxdgaming.spring.boot.core.lang.RunResult;
import wxdgaming.spring.boot.core.util.Md5Util;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.client.SocketClientConfig;
import wxdgaming.spring.boot.net.httpclient.HttpRequestPost;
import wxdgaming.spring.boot.net.httpclient.HttpResponse;
import wxdgaming.spring.boot.net.pojo.ProtoListenerFactory;
import wxdgaming.spring.boot.net.server.SocketServer;
import wxdgaming.spring.boot.scheduled.ann.Scheduled;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 网关和游戏服之间的连接管理服务
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-05-28 13:45
 **/
@Slf4j
@Getter
@ConfigurationPropertiesScan(basePackageClasses = {ClientForwardConfig.class})
@Service
public class Gateway2GameSessionService extends HoldRunApplication {

    final GatewayBootstrapConfig gatewayBootstrapConfig;
    final SocketServer socketServer;
    final ConcurrentHashMap<Integer, Gateway2GameSocketClientImpl> gameSessionMap = new ConcurrentHashMap<>();

    private final ProtoListenerFactory protoListenerFactory;
    private final SocketClientConfig socketClientConfig;

    public Gateway2GameSessionService(GatewayBootstrapConfig gatewayBootstrapConfig, SocketServer socketServer,
                                      ProtoListenerFactory protoListenerFactory, ClientForwardConfig clientForwardConfig) {
        this.gatewayBootstrapConfig = gatewayBootstrapConfig;
        this.socketServer = socketServer;
        this.protoListenerFactory = protoListenerFactory;
        this.socketClientConfig = clientForwardConfig;
    }

    @Start
    public void start() {
        Object o = runApplication.configValue("${socket.clientForward}", String.class);
    }

    /** 向登陆服务器注册 */
    @Scheduled(value = "*/5", async = true)
    @ExecutorWith(useVirtualThread = true)
    public void registerLoginServer() {

        InnerServerInfoBean serverInfoBean = new InnerServerInfoBean();
        serverInfoBean.setGid(gatewayBootstrapConfig.getGid());
        serverInfoBean.setServerId(gatewayBootstrapConfig.getSid());
        serverInfoBean.setMainId(gatewayBootstrapConfig.getSid());
        serverInfoBean.setName(gatewayBootstrapConfig.getName());
        serverInfoBean.setPort(socketServer.getConfig().getPort());
        serverInfoBean.setHttpPort(socketServer.getConfig().getPort());

        serverInfoBean.setMaxOnlineSize(gatewayBootstrapConfig.getMaxOnline());
        serverInfoBean.setOnlineSize(socketServer.getSessionGroup().size());

        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("sid", gatewayBootstrapConfig.getSid());
        jsonObject.put("serverBean", serverInfoBean.toJSONString());

        String json = jsonObject.toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
        String md5DigestEncode = Md5Util.md5DigestEncode0("#", json, gatewayBootstrapConfig.getJwtKey());
        jsonObject.put("sign", md5DigestEncode);

        String string = jsonObject.toString();
        HttpResponse httpResponse = HttpRequestPost.ofJson(gatewayBootstrapConfig.getLoginUrl() + "/inner/registerGateway", string).execute();
        if (!httpResponse.isSuccess()) {
            log.error("访问登陆服务器失败{}", Throw.ofString(httpResponse.getException(), false));
            return;
        }
        log.info("登录服务器注册完成返回信息: {}", httpResponse.bodyString());
        RunResult runResult = httpResponse.bodyRunResult();
        if (runResult.code() == 1) {

            InnerRegisterServer registerServer = new InnerRegisterServer();
            registerServer.setServiceType(ServiceType.GATEWAY);
            registerServer.setMainSid(gatewayBootstrapConfig.getSid());

            List<InnerServerInfoBean> data = runResult.getObject("data", new TypeReference<List<InnerServerInfoBean>>() {});
            HashSet<Integer> hasServerIdSet = new HashSet<>();
            for (InnerServerInfoBean bean : data) {
                hasServerIdSet.add(bean.getServerId());
                checkGatewaySession(bean.getServerId(), bean.getHost(), bean.getPort(), registerServer);
            }
            Iterator<Map.Entry<Integer, Gateway2GameSocketClientImpl>> iterator = getGameSessionMap().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Gateway2GameSocketClientImpl> next = iterator.next();
                if (!hasServerIdSet.contains(next.getKey())) {
                    next.getValue().shutdown();
                    iterator.remove();
                    SocketClientConfig config = next.getValue().getConfig();
                    log.info("sid={}, host={}, port={} 游戏服务下线，不在需要链接", next.getKey(), config.getHost(), config.getPort());
                    /*可能还需要对在改服的玩家进行处理*/
                }
            }
        }

    }

    /** 网关主动连游戏服 */
    public void checkGatewaySession(int sid, final String inetHost, final int inetPort, InnerRegisterServer registerServer) {
        Gateway2GameSocketClientImpl gatewaySocketClient = getGameSessionMap().computeIfAbsent(sid, l -> {
            SocketClientConfig newSocketClientConfig = (SocketClientConfig) socketClientConfig.clone();
            newSocketClientConfig.setHost(inetHost);
            newSocketClientConfig.setPort(inetPort);
            newSocketClientConfig.setMaxConnectionCount(1);
            newSocketClientConfig.setEnabledReconnection(false);
            Gateway2GameSocketClientImpl socketClient = new Gateway2GameSocketClientImpl(newSocketClientConfig);
            socketClient.init(protoListenerFactory);
            return socketClient;
        });

        gatewaySocketClient.checkSync(null);

        SocketSession socketSession = gatewaySocketClient.idle();
        if (socketSession != null) {
            if (socketSession.isOpen()) {
                log.info("{}", registerServer);
                socketSession.write(registerServer);
            }
        }
    }


}
