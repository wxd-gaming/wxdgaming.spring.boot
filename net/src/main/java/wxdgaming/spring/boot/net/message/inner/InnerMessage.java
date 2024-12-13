package wxdgaming.spring.boot.net.message.inner;

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
 * @version: 2024-12-13 15:41:05
 */
public class InnerMessage {
   /** 执行同步等待消息 */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ReqHeart extends PojoBase {
   
       /** 当前毫秒 */
       @Tag(1)
       private long milli;

   }

   /** 执行同步等待消息 */
   @Getter
   @Setter
   @Accessors(chain = true)
   public static class ResHeart extends PojoBase {
   
       /** 当前毫秒 */
       @Tag(1)
       private long milli;

   }

}