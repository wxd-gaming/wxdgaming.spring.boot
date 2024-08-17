package wxdgaming.spring.boot.message;


/**
 * protobuf 映射 基类
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-05-28 21:24
 **/
public class PojoBase {

    /** 编码 */
    public byte[] encode() {
        return SerializerUtil.encode(this);
    }

    /** 解码 */
    public void decode(byte[] bytes) {
        SerializerUtil.decode(bytes, this);
    }

}