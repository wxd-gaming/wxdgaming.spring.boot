package wxdgaming.spring.boot.net.module.inner.message;

import io.protostuff.Tag;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import wxdgaming.spring.boot.net.pojo.PojoBase;


/** 执行同步等待消息 */
@Getter
@Setter
@Accessors(chain = true)
public class ReqRemote extends PojoBase {

    /**  */
    @Tag(1) private long uid;
    /** 用于验证的消息 */
    @Tag(2) private String token;
    /** 执行的命令 */
    @Tag(3) private String cmd;
    /** 1表示压缩过 */
    @Tag(4) private int gzip;
    /** 用JsonObject来解析 */
    @Tag(5) private String params;

}
