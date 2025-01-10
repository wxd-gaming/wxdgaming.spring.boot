package wxdgaming.spring.boot.data.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * redis配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 13:54
 **/
@Slf4j
@Getter
@Setter
@Service
@ConfigurationProperties("spring.redis3")
@ConditionalOnProperty("spring.redis3.config.host")
public class Redis3Build extends RedisBuildBase {

    private RedisProperties config;

    @Bean("redisConnectionFactory3")
    public RedisConnectionFactory redisConnectionFactory() {
        return redisConnectionFactory(config);
    }

    @Bean("redisTemplate3")
    public RedisTemplate<String, Object> redisTemplate(
            @Qualifier("redisConnectionFactory3") RedisConnectionFactory redisConnectionFactory) {
        return buildRedisTemplate(redisConnectionFactory);
    }

}
