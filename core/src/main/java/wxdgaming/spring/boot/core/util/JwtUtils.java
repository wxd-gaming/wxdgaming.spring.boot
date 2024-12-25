package wxdgaming.spring.boot.core.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class JwtUtils {

    static byte[] skey = "6b1a678d78e302fd9d264256f17fbec8t".getBytes(StandardCharsets.UTF_8);
    static SecretKey key = Keys.hmacShaKeyFor(skey);

    public static void build() {
        createJwtBuilder();
        createJwtParser();
        /*调用一次实例化*/
        String jwt = JwtUtils.createJwtBuilder()
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(6)))
                .header().add("token", "3")
                .and()
                .claim("gameId", "1234")
                .claim("account", "admin")
                .compact();
        parseJWT(jwt);
    }

    public static JwtBuilder createJwtBuilder() {
        return createJwtBuilder(key);
    }

    public static JwtBuilder createJwtBuilder(String private_key) {
        return createJwtBuilder(Keys.hmacShaKeyFor(private_key.getBytes(StandardCharsets.UTF_8)));
    }

    public static JwtBuilder createJwtBuilder(SecretKey key) {
        // 生成 HMAC 密钥，根据提供的字节数组长度选择适当的 HMAC 算法，并返回相应的 SecretKey 对象。
        // 设置jwt的body
        return Jwts.builder()
                // 设置签名使用的签名算法和签名使用的秘钥
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .signWith(key);
    }

    public static JwtParser createJwtParser() {
        return createJwtParser(key);
    }

    public static JwtParser createJwtParser(String private_key) {
        return createJwtParser(Keys.hmacShaKeyFor(private_key.getBytes(StandardCharsets.UTF_8)));
    }

    public static JwtParser createJwtParser(SecretKey key) {
        // 生成 HMAC 密钥，根据提供的字节数组长度选择适当的 HMAC 算法，并返回相应的 SecretKey 对象。
        // 设置jwt的body
        return Jwts.parser()
                // 设置签名使用的签名算法和签名使用的秘钥
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .verifyWith(key)
                .build();
    }

    public static Jws<Claims> parseJWT(String token) {
        return parseJWT(createJwtParser(key), token);
    }

    public static Jws<Claims> parseJWT(String private_key, String token) {
        return parseJWT(createJwtParser(private_key), token);
    }

    /**
     * Token解密
     *
     * @param jwtParser
     * @param token
     * @return
     * @author: wxd-gaming(無心道, 15388152619)
     * @version: 2024-10-17 19:43
     */
    public static Jws<Claims> parseJWT(JwtParser jwtParser, String token) {
        // 生成 HMAC 密钥，根据提供的字节数组长度选择适当的 HMAC 算法，并返回相应的 SecretKey 对象。
        // 得到DefaultJwtParser
        return jwtParser.parseSignedClaims(token);
    }
}
