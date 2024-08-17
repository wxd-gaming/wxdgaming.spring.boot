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
 * @version: 2024-08-17 21:19:24
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
       /** 1表示压缩过 */
       @Tag(2)
       private int gzip;
       /** 执行的命令 */
       @Tag(3)
       private String cmd;
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
       /** 1表示压缩过 */
       @Tag(2)
       private int gzip;
       /** 用JsonObject来解析 */
       @Tag(3)
       private String params;
       /** 用于验证的消息 */
       @Tag(4)
       private String rpcToken;

   }
}