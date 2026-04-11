package com.github.iappapp.panda.common.redis.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import reactor.core.publisher.Flux;

@Configuration
@ConditionalOnClass({ReactiveRedisConnectionFactory.class, ReactiveRedisTemplate.class, Flux.class})
@EnableConfigurationProperties({RedisProperties.class})
public class RedisReactiveAutoConfiguration {
  @Autowired
  private RedisProperties redisProperties;
  
  @Bean
  @ConditionalOnMissingClass({"redis.clients.jedis.Jedis"})
  public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory, ResourceLoader resourceLoader) {
    JdkSerializationRedisSerializer jdkSerializer =
            new JdkSerializationRedisSerializer(resourceLoader.getClassLoader());
    RedisSerializer<Object> valueRedisSerializer = this.redisProperties.getValueSerializer();
    RedisSerializationContext.RedisSerializationContextBuilder<String, Object> builder =
            RedisSerializationContext.newSerializationContext(new StringRedisSerializer());
    RedisSerializationContext<String, Object> serializationContext = builder.value(valueRedisSerializer).build();
    return new ReactiveRedisTemplate(reactiveRedisConnectionFactory, serializationContext);
  }
  
  @Bean
  @ConditionalOnMissingClass({"redis.clients.jedis.Jedis"})
  public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
    return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
  }
}
