package com.github.iappapp.panda.common.redis.config;

import java.net.UnknownHostException;
import java.util.LinkedHashSet;
import java.util.List;

import com.github.iappapp.panda.common.redis.condition.RedisCacheCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@EnableCaching
@EnableConfigurationProperties({RedisProperties.class, CacheProperties.class})
public class RedisAutoConfiguration {
  private static final Logger log = LoggerFactory.getLogger(RedisAutoConfiguration.class);
  
  @Autowired
  private RedisProperties redisProperties;
  
  @Autowired
  private CacheProperties cacheProperties;
  
  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> redisTemplate = new RedisTemplate();
    redisTemplate.setConnectionFactory(connectionFactory);
    StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
    redisTemplate.setDefaultSerializer(stringRedisSerializer);
    redisTemplate.setEnableDefaultSerializer(true);
    redisTemplate.setKeySerializer(stringRedisSerializer);
    redisTemplate.setHashKeySerializer(stringRedisSerializer);
    RedisSerializer<Object> valueRedisSerializer = this.redisProperties.getValueSerializer();
    redisTemplate.setValueSerializer(valueRedisSerializer);
    redisTemplate.setHashValueSerializer(valueRedisSerializer);
    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }
  
  @Bean
  public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory)
          throws UnknownHostException {
    StringRedisTemplate template = new StringRedisTemplate();
    template.setConnectionFactory(redisConnectionFactory);
    return template;
  }
  
  @Conditional({RedisCacheCondition.class})
  @Bean
  public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory, ResourceLoader resourceLoader) {
    RedisCacheManager.RedisCacheManagerBuilder builder =
            RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(determineConfiguration(resourceLoader.getClassLoader()));
    List<String> cacheNames = this.cacheProperties.getCacheNames();
    if (!cacheNames.isEmpty()) {
      builder.initialCacheNames(new LinkedHashSet<>(cacheNames));
    }
    return builder.build();
  }
  
  private RedisCacheConfiguration determineConfiguration(ClassLoader classLoader) {
    CacheProperties.Redis redisProperties = this.cacheProperties.getRedis();
    RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
    RedisSerializer<Object> valueRedisSerializer = this.redisProperties.getValueSerializer();
    config = config.serializeValuesWith(
        RedisSerializationContext.SerializationPair.fromSerializer(valueRedisSerializer));
    if (redisProperties.getTimeToLive() != null) {
      config = config.entryTtl(redisProperties.getTimeToLive());
    }
    if (redisProperties.getKeyPrefix() != null) {
      config = config.prefixKeysWith(redisProperties.getKeyPrefix());
    }
    if (!redisProperties.isCacheNullValues()) {
      config = config.disableCachingNullValues();
    }
    if (!redisProperties.isUseKeyPrefix()) {
      config = config.disableKeyPrefix();
    }
    return config;
  }
}
