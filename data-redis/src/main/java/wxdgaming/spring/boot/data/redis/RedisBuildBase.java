package wxdgaming.spring.boot.data.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import wxdgaming.spring.boot.core.InitPrint;

/**
 * redis配置
 *
 * @author: wxd-gaming(無心道, 15388152619)
 * @version: 2024-08-08 13:54
 **/
@Slf4j
public abstract class RedisBuildBase implements CachingConfigurer, InitPrint {

    static RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setUsername(redisProperties.getUsername());

        GenericObjectPoolConfig<?> poolConfig = buildGenericObjectPoolConfig(redisProperties);

        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder();
        builder.poolConfig(poolConfig);
        if (redisProperties.getTimeout() != null)
            builder.commandTimeout(redisProperties.getTimeout());

        return new LettuceConnectionFactory(redisStandaloneConfiguration, builder.build());
    }

    static GenericObjectPoolConfig<?> buildGenericObjectPoolConfig(RedisProperties redisProperties) {
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
        poolConfig.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
        poolConfig.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());
        if (redisProperties.getLettuce().getPool().getMaxWait() != null) {
            poolConfig.setMaxWait(redisProperties.getLettuce().getPool().getMaxWait());
        }
        return poolConfig;
    }

    static RedisTemplate<String, Object> buildRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
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
