package wxdgaming.spring.boot.core.token;

import com.alibaba.fastjson.serializer.SerializerFeature;
import wxdgaming.spring.boot.core.chatset.Base64Util;
import wxdgaming.spring.boot.core.chatset.json.FastJsonUtil;
import wxdgaming.spring.boot.core.util.AesUtil;
import wxdgaming.spring.boot.core.util.Md5Util;

import java.util.concurrent.TimeUnit;

/**
 * 构建器
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2025-06-16 13:59
 **/
public class JsonTokenBuilder {

    protected static final int[] ENCODE_KK = {2, 7, 3};
    protected static final int[] DECODE_KK = {3, 7, 2};

    /**
     * 构建器， 默认5分钟过期
     *
     * @param key 私钥
     */
    public static JsonTokenBuilder of(String key) {
        return of(key, TimeUnit.MINUTES, 5);/*默认五分钟有效期*/
    }

    /**
     * 构建器
     *
     * @param key      加密私钥
     * @param timeUnit 过期时间 单位
     * @param expire   过期时间
     */
    public static JsonTokenBuilder of(String key, TimeUnit timeUnit, long expire) {
        return new JsonTokenBuilder(key).expire(timeUnit, expire);
    }

    private final String key;
    private final JsonToken jsonToken = new JsonToken();

    private JsonTokenBuilder(String key) {
        this.key = key;
    }

    /** 设置过期时间 */
    public JsonTokenBuilder expire(TimeUnit timeUnit, long duration) {
        jsonToken.setExpire(System.currentTimeMillis() + timeUnit.toMillis(duration));
        return this;
    }

    /** 填充数据 */
    public JsonTokenBuilder put(String key, Object value) {
        jsonToken.getData().put(key, value);
        return this;
    }

    /** 生成加密字符串 */
    public String compact() {
        String dataString = jsonToken.getData().toString(SerializerFeature.MapSortField, SerializerFeature.SortField);
        jsonToken.setSignature(Md5Util.md5DigestEncode0("#", dataString, key));
        byte[] jsonString = FastJsonUtil.toJSONBytes(jsonToken);
        String encode = Base64Util.encode2String(jsonString);
        encode = AesUtil.convert_ASE(encode, ENCODE_KK);
        return encode;
    }

    /** 查看当前数据 */
    public String viewData() {
        return jsonToken.toString();
    }

}
