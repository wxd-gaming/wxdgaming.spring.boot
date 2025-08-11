package wxdgaming.spring.boot.net.pojo;


import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.lang.ObjectBase;

import java.io.Serial;
import java.io.Serializable;

/**
 * protobuf 映射 基类
 *
 * @author wxd-gaming(無心道, 15388152619)
 * @version 2024-05-28 21:24
 **/
public abstract class PojoBase extends ObjectBase implements Serializable {

    @Serial private static final long serialVersionUID = 1L;

    public int msgId() {
        return 0;
    }

    /** 编码 */
    public byte[] encode() {
        return SerializerUtil.encode(this);
    }

    /** 解码 */
    public void decode(byte[] bytes) {
        SerializerUtil.decode(bytes, this);
    }

    @Override public String toString() {
        return this.getClass().getSimpleName() + FastJsonUtil.toJSONString(this);
    }

}
