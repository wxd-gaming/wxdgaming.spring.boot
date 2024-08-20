package wxdgaming.spring.boot.rpc.pojo;

import io.protostuff.Tag;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.message.PojoBase;


/**
 * rpc.proto
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-20 11:30:19
 */
public class RpcMessage {

   /** 执行同步等待消息 */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ReqRemote extends PojoBase {

       /**  */
       @Tag(1)
       private long rpcId;
       /** 执行的命令 */
       @Tag(3)
       private String path;
       /** 用JsonObject来解析 */
       @Tag(4)
       private String params;
       /** 用于验证的消息 */
       @Tag(5)
       private String rpcToken;

   }

   /** 执行同步等待消息 */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ResRemote extends PojoBase {

       /**  */
       @Tag(1)
       private long rpcId;
       /** 用于验证的消息 */
       @Tag(3)
       private String rpcToken;
       /** code==1 params 是正常参数，如果非1是错误码 */
       @Tag(4)
       private int code;
       /** 用JsonObject来解析 */
       @Tag(5)
       private String params;

   }
}