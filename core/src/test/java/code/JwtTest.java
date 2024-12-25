package code;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.junit.Test;
import wxdgaming.spring.boot.core.util.JwtUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 密钥令牌
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-10-17 15:10
 **/
public class JwtTest {


    @Test
    public void generateJWT() {
        String jwt = JwtUtils.createJwtBuilder()
                .expiration(new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(6)))
                .header().add("token", "3")
                .and()
                .claim("gameId", "1234")
                .claim("account", "admin")
                .compact();

        System.out.println("Generated JWT: " + jwt);
    }

    /**
     * 服务器解析JWT
     */
    @Test
    public void verifyJWT() {
        // 假设这是从客户端接收到的 JWT
        String jwt = "eyJ0b2tlbiI6IjMiLCJhbGciOiJIUzI1NiJ9.eyJleHAiOjE3MjkxNjYxNTEsImdhbWVJZCI6IjEyMzQiLCJhY2NvdW50IjoiYWRtaW4ifQ.ZsnZUl66XkDylk0clS4GLFhSabXqiXJhCIQPpiUAIhQ";
        Jws<Claims> claimsJws = JwtUtils.parseJWT(jwt);
        Claims claims = claimsJws.getPayload();
        System.out.println("gameId: " + claims.get("gameId"));
        System.out.println("account: " + claims.get("account"));

    }


}
