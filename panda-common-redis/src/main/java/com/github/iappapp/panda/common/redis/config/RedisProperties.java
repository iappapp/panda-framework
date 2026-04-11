package com.github.iappapp.panda.common.redis.config;

import com.alibaba.fastjson.parser.ParserConfig;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.annotation.PostConstruct;

import com.github.iappapp.panda.common.redis.constant.RedisSerializerTypeEnum;
import com.github.iappapp.panda.common.redis.serializer.FastJson2JsonRedisSerializer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@ConfigurationProperties(prefix = "panda.redis")
public class RedisProperties {

  
  private String valueSerializerType = RedisSerializerTypeEnum.JACKSON.name();
  
  private RedisSerializer<Object> valueSerializer;

  public void setValueSerializerType(String valueSerializerType) {
    this.valueSerializerType = valueSerializerType;
  }

  public void setValueSerializer(RedisSerializer<Object> valueSerializer) {
    this.valueSerializer = valueSerializer;
  }
  
  public String getValueSerializerType() {
    return this.valueSerializerType;
  }
  
  public RedisSerializer<Object> getValueSerializer() {
    return this.valueSerializer;
  }
  
  @PostConstruct
  void init() {
    this.valueSerializer = parseRedisSerializer(this.valueSerializerType);
  }
  
  private RedisSerializer<Object> parseRedisSerializer(String redisSerializerType) {
    Jackson2JsonRedisSerializer<Object> redisSerializer;
    ObjectMapper mapper;
    RedisSerializerTypeEnum redisSerializerTypeEnum = RedisSerializerTypeEnum.valueOf(redisSerializerType);
    if (redisSerializerTypeEnum == null) {
      redisSerializerTypeEnum = RedisSerializerTypeEnum.JACKSON;
    }
    switch (redisSerializerTypeEnum) {
      case JACKSON:
        redisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        redisSerializer.setObjectMapper(mapper);
        return redisSerializer;
      case FAST_JSON:
        ParserConfig.getGlobalInstance().setAsmEnable(true);
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
        return new FastJson2JsonRedisSerializer(Object.class);
      case JDK:
        return new JdkSerializationRedisSerializer();
      default:
        break;
    } 
    return new JdkSerializationRedisSerializer();
  }
}
