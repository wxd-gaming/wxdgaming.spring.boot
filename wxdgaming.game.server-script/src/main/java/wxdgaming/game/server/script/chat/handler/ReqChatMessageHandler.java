package wxdgaming.game.server.script.chat.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.game.message.chat.ReqChatMessage;
import wxdgaming.game.server.GameServiceBootstrapConfig;
import wxdgaming.game.server.bean.global.GlobalDataType;
import wxdgaming.game.server.bean.global.impl.YunyingData;
import wxdgaming.game.server.bean.role.Player;
import wxdgaming.game.server.module.data.DataCenterService;
import wxdgaming.game.server.module.data.GlobalDataService;
import wxdgaming.game.server.script.chat.ChatService;
import wxdgaming.game.server.script.gm.GmService;
import wxdgaming.game.server.script.tips.TipsService;
import wxdgaming.spring.boot.core.ann.ThreadParam;
import wxdgaming.spring.boot.net.SocketSession;
import wxdgaming.spring.boot.net.ann.ProtoRequest;

/**
 * 请求聊天
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: v1.1
 **/
@Slf4j
@Component
public class ReqChatMessageHandler {

    private final GameServiceBootstrapConfig gameServiceBootstrapConfig;
    private final GlobalDataService globalDataService;
    private final DataCenterService dataCenterService;
    private final ChatService chatService;
    private final TipsService tipsService;
    private final GmService gmService;

    public ReqChatMessageHandler(GameServiceBootstrapConfig gameServiceBootstrapConfig, GlobalDataService globalDataService,
                                 DataCenterService dataCenterService,
                                 ChatService chatService,
                                 TipsService tipsService,
                                 GmService gmService) {
        this.gameServiceBootstrapConfig = gameServiceBootstrapConfig;
        this.globalDataService = globalDataService;
        this.dataCenterService = dataCenterService;
        this.chatService = chatService;
        this.tipsService = tipsService;
        this.gmService = gmService;
    }

    /** 请求聊天 */
    @ProtoRequest
    public void reqChatMessage(SocketSession socketSession, ReqChatMessage req,
                               @ThreadParam(path = "player") Player player) {
        String content = req.getContent();
        log.info("{} 聊天消息 {}", player, req);
        if (content.startsWith("@gm")) {
            YunyingData yunyingData = globalDataService.get(GlobalDataType.YUNYINGDATA);
            if (gameServiceBootstrapConfig.isDebug()
                || yunyingData.getGmAccountSet().contains(player.getAccount())
                || yunyingData.getGmPlayerIdSet().contains(player.getUid())) {
                gmService.doGm(player, content.substring(3).trim().split(" "));
                return;
            }
        }
        if (content.length() > 120) {
            tipsService.tips(player, "消息长度不能超过100");
            return;
        }
        if (player.getLevel() < 10) {
            tipsService.tips(player, "等级不足10级");
            return;
        }

        /*TODO敏感词过滤*/
        String replace = dataCenterService.getKeywordsMapping().replace(content, '*');
        req.setContent(replace);

        chatService.chatHandler(req.getType()).chat(player, req);
    }

}