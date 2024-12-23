package wxdgaming.spring.boot.rpc.pojo;

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
 * @version: 2024-12-23 13:18:42
 */
public class RpcMessage {
   /** 创建连接后验证token */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ReqRPCCheckToken extends PojoBase {
   
       /**  */
       @Tag(1)
       private String token;

   }

   /** 创建连接后验证token */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ResRPCCheckToken extends PojoBase {
   
       /**  */
       @Tag(1)
       private String token;

   }

   /** 执行同步等待消息 */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ReqRPC extends PojoBase {
   
       /**  */
       @Tag(1)
       private long rpcId;
       /** 目标id */
       @Tag(2)
       private long targetId;
       /** 执行的命令 */
       @Tag(3)
       private String path;
       /** 用JsonObject来解析 */
       @Tag(4)
       private String params;

   }

   /** 执行同步等待消息 */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ResRPC extends PojoBase {
   
       /**  */
       @Tag(1)
       private long rpcId;
       /** 目标id */
       @Tag(2)
       private long targetId;
       /** code==1 params 是正常参数，如果非1是错误码 */
       @Tag(4)
       private int code;
       /** 用JsonObject来解析 */
       @Tag(5)
       private String params;

   }

   /** 转发消息 */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ReqBroker extends PojoBase {
   
       /**  */
       @Tag(1)
       private long uId;
       /** 目标id */
       @Tag(2)
       private List<Long> targetId;
       /** 消息id */
       @Tag(3)
       private int msgId;
       /** 消息报文 */
       @Tag(4)
       private byte[] msgData;

   }

   /** 转发消息 回执 */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ResBroker extends PojoBase {
   
       /**  */
       @Tag(1)
       private long uId;
       /** 目标id */
       @Tag(2)
       private List<Long> targetId;
       /** 消息id */
       @Tag(3)
       private int msgId;
       /** 消息报文 */
       @Tag(4)
       private byte[] msgData;

   }

}