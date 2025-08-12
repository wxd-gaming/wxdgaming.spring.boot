package wxdgaming.game.server.module.timer;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wxdgaming.game.login.LoginConfig;
import wxdgaming.game.login.bean.info.InnerServerInfoBean;
import wxdgaming.game.server.module.drive.PlayerDriveService;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.boot.core.executor.ExecutorWith;
import wxdgaming.spring.boot.core.util.Md5Util;
import wxdgaming.spring.boot.net.httpclient.HttpRequestPost;
import wxdgaming.spring.boot.net.httpclient.HttpResponse;
import wxdgaming.spring.boot.net.server.SocketServer;
import wxdgaming.spring.boot.scheduled.ann.Scheduled;

import java.util.List;

/**
 * 游戏进程的定时器服务
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-11 20:22
 **/
@Slf4j
@Service
public class GameTimerService {

    private final int sid;
    private final int gid;
    private final String sname;
    final SocketServer socketServer;
    final LoginConfig loginConfig;
    final PlayerDriveService playerDriveService;

    public GameTimerService(@Value("${sid}") int sid, @Value("${gid}") int gid,@Value("${sname}") String sname,
                            SocketServer socketServer, LoginConfig loginConfig, PlayerDriveService playerDriveService) {
        this.sid = sid;
        this.gid = gid;
        this.sname = sname;
        this.socketServer = socketServer;
        this.loginConfig = loginConfig;
        this.playerDriveService = playerDriveService;
    }


    /** 向登陆服务器注册 */
    @Scheduled(value = "*/5", async = true)
    @ExecutorWith(useVirtualThread = true)
    public void registerLoginServer() {

        InnerServerInfoBean serverInfoBean = new InnerServerInfoBean();
        serverInfoBean.setServerId(sid);
        serverInfoBean.setMainId(sid);
        serverInfoBean.setGid(gid);
        serverInfoBean.setName(sname);
        serverInfoBean.setPort(socketServer.getConfig().getPort());
        serverInfoBean.setHttpPort(socketServer.getConfig().getPort());

        serverInfoBean.setMaxOnlineSize(loginConfig.getMaxOnlineSize());
        serverInfoBean.setOnlineSize(playerDriveService.onlineSize());


        JSONObject jsonObject = MapOf.newJSONObject();
        jsonObject.put("sidList", List.of(sid));
        jsonObject.put("sid", sid);
        jsonObject.put("serverBean", serverInfoBean.toJSONString());

        String json = jsonObject.toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
        String md5DigestEncode = Md5Util.md5DigestEncode0("#", json, loginConfig.getJwtKey());
        jsonObject.put("sign", md5DigestEncode);

        HttpResponse execute = HttpRequestPost.ofJson(loginConfig.getUrl() + "/inner/registerGame", jsonObject.toString()).execute();
        log.info("向登陆服务器注册: {}", execute.bodyString());
    }

}
