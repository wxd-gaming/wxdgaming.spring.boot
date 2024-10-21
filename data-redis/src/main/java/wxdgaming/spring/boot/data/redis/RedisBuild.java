package wxdgaming.spring.boot.data.redis;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
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


    private RedisProperties first;
    private RedisProperties second;

    @Primary
    @Bean("redisConnectionFactory")
    @ConditionalOnProperty("spring.redis.first.host")
    public RedisConnectionFactory redisConnectionFactory() {
        return redisConnectionFactory(first);
    }

    @Primary
    @Bean("redisTemplate")
    @ConditionalOnProperty("spring.redis.first.host")
    public RedisTemplate<String, Object> redisTemplate(
            @Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        return buildRedisTemplate(redisConnectionFactory);
    }

    @Bean("secondRedisConnectionFactory")
    @ConditionalOnProperty("spring.redis.second.host")
    public RedisConnectionFactory secondRedisConnectionFactory() {
        return redisConnectionFactory(second);
    }

    @Bean("secondRedisTemplate")
    @ConditionalOnProperty("spring.redis.second.host")
    public RedisTemplate<String, Object> secondRedisTemplate(
            @Qualifier("secondRedisConnectionFactory") RedisConnectionFactory secondRedisConnectionFactory) {
        return buildRedisTemplate(secondRedisConnectionFactory);
    }

    RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
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

    private static GenericObjectPoolConfig<?> buildGenericObjectPoolConfig(RedisProperties redisProperties) {
        GenericObjectPoolConfig<?> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(redisProperties.getLettuce().getPool().getMaxActive());
        poolConfig.setMaxIdle(redisProperties.getLettuce().getPool().getMaxIdle());
        poolConfig.setMinIdle(redisProperties.getLettuce().getPool().getMinIdle());
        if (redisProperties.getLettuce().getPool().getMaxWait() != null) {
            poolConfig.setMaxWait(redisProperties.getLettuce().getPool().getMaxWait());
        }
        return poolConfig;
    }

    RedisTemplate<String, Object> buildRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
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
