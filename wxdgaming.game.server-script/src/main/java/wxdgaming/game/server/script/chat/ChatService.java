package wxdgaming.game.server.script.chat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import wxdgaming.spring.boot.core.HoldRunApplication;
import wxdgaming.spring.boot.core.ann.Init;
import wxdgaming.spring.boot.core.util.AssertUtil;
import wxdgaming.game.message.chat.ChatType;

import java.util.HashMap;

/**
 * 聊天模块
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2025-04-28 19:30
 **/
@Slf4j
@Component
public class ChatService extends HoldRunApplication {

    HashMap<ChatType, AbstractChatAction> chatHandlerMap = new HashMap<>();

    @Init
    public void init() {
        HashMap<ChatType, AbstractChatAction> tmpChatHandlerMap = new HashMap<>();
        runApplication.classWithSuper(AbstractChatAction.class)
                .forEach(abstractChatAction -> {
                    AbstractChatAction put = tmpChatHandlerMap.put(abstractChatAction.chatType(), abstractChatAction);
                    AssertUtil.assertTrue(put == null, "重复注册类型：" + abstractChatAction.chatType());
                });
        this.chatHandlerMap = tmpChatHandlerMap;
    }

    public AbstractChatAction chatHandler(ChatType chatType) {
        return chatHandlerMap.getOrDefault(chatType, chatHandlerMap.get(ChatType.Chat_TYPE_NONE));
    }

}
