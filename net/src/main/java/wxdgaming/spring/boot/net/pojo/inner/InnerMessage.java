package wxdgaming.spring.boot.net.pojo.inner;

import io.protostuff.Tag;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.net.message.PojoBase;


/**
 * rpc.proto
 *
 * @author: wxd-gaming(無心道, 15388152619)
<<<<<<<< HEAD:net/src/main/java/wxdgaming/spring/boot/net/message/inner/InnerMessage.java
 * @version: 2024-09-21 20:39:35
========
 * @version: 2024-09-26 20:14:03
>>>>>>>> 47bc7aba3c7413b36452d8aceeba054e4f7e72ad:net/src/main/java/wxdgaming/spring/boot/net/pojo/inner/InnerMessage.java
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