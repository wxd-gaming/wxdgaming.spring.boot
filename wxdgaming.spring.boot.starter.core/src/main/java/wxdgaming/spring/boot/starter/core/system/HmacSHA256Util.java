package wxdgaming.spring.boot.starter.core.system;

import wxdgaming.spring.boot.starter.core.Throw;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.Serializable;

/**
 * HmacSHA256算法
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2022-06-30 11:16
 **/
public class HmacSHA256Util implements Serializable {
    /**
     * HmacSHA256算法,返回的结果始终是32位
     *
     * @param key     加密的键，可以是任何数据
     * @param content 待加密的内容
     * @return 加密后的内容
     * @throws Exception
     */
    public static byte[] hmacSHA256(byte[] key, byte[] content) {
        try {
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            hmacSha256.init(new SecretKeySpec(key, 0, key.length, "HmacSHA256"));
            byte[] hmacSha256Bytes = hmacSha256.doFinal(content);
            return hmacSha256Bytes;
        } catch (Exception e) {
            throw Throw.of(e);
        }
    }

    /**
     * 将加密后的字节数组转换成字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    /**
     * sha256_HMAC加密
     *
     * @param secret  秘钥
     * @param message 消息
     * @return 加密后字符串
     */
    public static String hmacSHA256(String secret, String message) {
        try {
            String hash = "";
            Mac hmacSha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            hmacSha256.init(secret_key);
            byte[] bytes = hmacSha256.doFinal(message.getBytes());
            hash = byteArrayToHexString(bytes);
            return hash;
        } catch (Exception e) {
            throw Throw.of(e);
        }
    }

    public void t0(String[] args) {
        String appSecret = "b06c75b58d1701ff470119a4114f8b45";
        String appId = "10000001";
        String timestamp = "1529853639000";

        //        HMAC-SHA256(appSecret，appId + timestamp+去除节点间的空格及换行符的请求体)
        String message = appId + timestamp + "<req><orderId>91303183862452</orderId><notifyUrl></notifyUrl><refundTime>2013-03-22 16:25:26</refundTime><refundFee>24.5</refundFee><refundNo>14252414245</refundNo></req>";
        System.out.println("加密前 =========>" + message);
        String str = "";
        try {
            str = HmacSHA256Util.hmacSHA256(appSecret, message);
            System.out.println("加密后 =========>" + str);
        } catch (Exception e) {
            System.out.println("Error HmacSHA256 ===========>" + e.getMessage());
        }

    }
}
