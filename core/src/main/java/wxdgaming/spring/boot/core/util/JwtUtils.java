package wxdgaming.spring.boot.core.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtUtils {

    static byte[] skey = "6b1a678d78e302fd9d264256f17fbec8t".getBytes(StandardCharsets.UTF_8);
    static SecretKey key = Keys.hmacShaKeyFor(skey);

    public static JwtBuilder createJwt() {
        return createJwt(key);
    }

    public static JwtBuilder createJwt(String private_key) {
        return createJwt(Keys.hmacShaKeyFor(private_key.getBytes(StandardCharsets.UTF_8)));
    }

    public static JwtBuilder createJwt(SecretKey key) {
        // 生成 HMAC 密钥，根据提供的字节数组长度选择适当的 HMAC 算法，并返回相应的 SecretKey 对象。
        // 设置jwt的body
        return Jwts.builder()
                // 设置签名使用的签名算法和签名使用的秘钥
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .signWith(key);
    }

    public static Jws<Claims> parseJWT(String token) {
        return parseJWT(key, token);
    }

    public static Jws<Claims> parseJWT(String private_key, String token) {
        return parseJWT(Keys.hmacShaKeyFor(private_key.getBytes(StandardCharsets.UTF_8)), token);
    }

    /**
     * Token解密
     *
     * @param key
     * @param token
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-10-17 19:43
     */
    public static Jws<Claims> parseJWT(SecretKey key, String token) {
        // 生成 HMAC 密钥，根据提供的字节数组长度选择适当的 HMAC 算法，并返回相应的 SecretKey 对象。
        // 得到DefaultJwtParser
        JwtParser jwtParser = Jwts.parser()
                // 设置签名的秘钥
                .verifyWith(key)
                .build();
        return jwtParser.parseSignedClaims(token);
    }
}
