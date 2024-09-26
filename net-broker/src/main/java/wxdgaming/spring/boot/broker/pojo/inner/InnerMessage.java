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
 * @version: 2024-09-26 21:04:23
 */
public class InnerMessage {

    /** 服务器类型 */
    @Getter
    public enum Stype {
    
       /** 游戏服 */
       @Tag(0)
       GAME(0, "游戏服"),
       /** broker */
       @Tag(1)
       BROKER(1, "broker"),
       /** 社交服 */
       @Tag(2)
       CHAT(2, "社交服"),

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
   public static class ReqRegister extends PojoBase {

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


   }

   /** 服务注册 */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ResRegister extends PojoBase {

       /** 服务器id */
       @Tag(1)
       private int sid;

       /** 服务器类型 */
       @Tag(2)
       private int stype;


   }
}