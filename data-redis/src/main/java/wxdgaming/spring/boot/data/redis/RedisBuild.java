package wxdgaming.spring.boot.data.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;
import wxdgaming.spring.boot.core.InitPrint;

/**
 * redis配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 13:54
 **/
@Slf4j
@Getter
@Setter
@EnableCaching
@Service
@ConfigurationProperties(prefix = "spring.redis")
public class RedisBuild implements CachingConfigurer, InitPrint {

    @Getter
    @Setter
    public static class Builder {
        private String host;
        private int port;
        private String password;
        /** redis index db0 db1 */
        private int database = 0;
    }

    private Builder first;
    private Builder second;

    @Bean("redisConnectionFactory")
    @ConditionalOnProperty("spring.redis.first.host")
    public RedisConnectionFactory redisConnectionFactory() {
        return redisConnectionFactory(first);
    }

    @Bean("redisTemplate")
    @Primary
    @ConditionalOnProperty("spring.redis.first.host")
    public RedisTemplate<?, ?> redisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        return build(redisConnectionFactory);
    }

    @Bean("secondRedisConnectionFactory")
    @ConditionalOnProperty("spring.redis.second.host")
    public RedisConnectionFactory secondRedisConnectionFactory() {
        return redisConnectionFactory(second);
    }

    @Bean("secondRedisTemplate")
    @ConditionalOnProperty("spring.redis.second.host")
    public RedisTemplate<?, ?> secondRedisTemplate(@Qualifier("secondRedisConnectionFactory") RedisConnectionFactory secondRedisConnectionFactory) {
        return build(secondRedisConnectionFactory);
    }

    RedisConnectionFactory redisConnectionFactory(Builder builder) {
        RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration();
        standaloneConfiguration.setHostName(builder.getHost());
        standaloneConfiguration.setPort(builder.getPort());
        standaloneConfiguration.setPassword(builder.getPassword());
        standaloneConfiguration.setDatabase(builder.getDatabase());
        return new JedisConnectionFactory(standaloneConfiguration);
    }

    RedisTemplate<?, ?> build(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<?, ?> redisTemplate = new RedisTemplate<>();
        // 默认的序列化器： new JdkSerializationRedisSerializer()
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        log.debug("redisTemplate hashCode: {}", redisTemplate.hashCode());
        return redisTemplate;
    }
}
