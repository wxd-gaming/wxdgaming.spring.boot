package wxdgaming.spring.boot.broker.pojo.inner;

import io.protostuff.Tag;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.core.collection.MapOf;
import wxdgaming.spring.boot.net.message.PojoBase;


/**
 * rpc.proto
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-11-22 21:02:04
 */
public class InnerMessage {

    /** 服务器类型 */
    @Getter
    public enum Stype {
    
       /** broker */
       @Tag(0)
       BROKER(0, "broker"),
       /** 游戏服 */
       @Tag(1)
       GAME(1, "游戏服"),
       /** 场景服 */
       @Tag(2)
       SCENE(2, "场景服"),
       /** 社交服 */
       @Tag(3)
       CHAT(3, "社交服"),
       /** 匹配服 */
       @Tag(4)
       MATCH(4, "匹配服"),
       /** 跨服 */
       @Tag(5)
       CROSS_GAME(5, "跨服"),
       /** 自定义1 */
       @Tag(6)
       OTHER1(6, "自定义1"),
       /** 自定义2 */
       @Tag(7)
       OTHER2(7, "自定义2"),
       /** 自定义3 */
       @Tag(8)
       OTHER3(8, "自定义3"),
       /** 自定义4 */
       @Tag(9)
       OTHER4(9, "自定义4"),

        ;

        private static final Map<Integer, Stype> static_map = MapOf.asMap(Stype::getCode, Stype.values());

        public static Stype valueOf(int code) {
            return static_map.get(code);
        }

        /** code */
        private final int code;
        /** 备注 */
        private final String command;

        Stype(int code, String command) {
            this.code = code;
            this.command = command;
        }
    }

   /** 服务注册 */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ReqBrokerRegister extends PojoBase {
   
       /** 服务器id */
       @Tag(1)
       private int sid;
       /** 服务器类型 */
       @Tag(2)
       private Stype stype;
       /** 外围ip */
       @Tag(3)
       private String wlanIp;
       /** 内网ip */
       @Tag(4)
       private String lanIp;
       /** 监听的消息id */
       @Tag(5)
       private List<Integer> listenMessageId;

   }

   /** 服务注册 */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ResBrokerRegister extends PojoBase {
   
       /** 服务器id */
       @Tag(1)
       private int sid;
       /** 服务器类型 */
       @Tag(2)
       private Stype stype;

   }

   /** null */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ReqBrokerMessage extends PojoBase {
   
       /** 服务器id */
       @Tag(1)
       private int sid;
       /** 转发消息 */
       @Tag(2)
       private bytes data;

   }

}