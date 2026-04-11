package com.github.iappapp.panda.common.redis.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

@Configuration
@ConditionalOnClass({Redisson.class, RedisOperations.class})
@EnableConfigurationProperties({RedissonProperties.class, RedisProperties.class})
public class RedissonAutoConfiguration {
  @Autowired
  private RedissonProperties redissonProperties;
  
  @Autowired
  private RedisProperties redisProperties;
  
  @Autowired
  private ApplicationContext ctx;
  
  private static String[] convert(List<String> nodesObject) {
    List<String> nodes = new ArrayList<>(nodesObject.size());
    if (!CollectionUtils.isEmpty(nodesObject)) {
      for (String node : nodesObject) {
        if (!node.startsWith("redis://") && !node.startsWith("rediss://")) {
          nodes.add("redis://" + node);
          continue;
        } 
        nodes.add(node);
      }
    }
    return nodes.<String>toArray(new String[0]);
  }
  
  @Bean
  @ConditionalOnMissingClass({"redis.clients.jedis.Jedis", "io.lettuce.core.RedisClient"})
  public RedissonConnectionFactory redissonConnectionFactory(RedissonClient redisson) {
    return new RedissonConnectionFactory(redisson);
  }
  
  @Bean(destroyMethod = "shutdown")
  public RedissonClient redisson() throws IOException {
    Config config = new Config();
    Method clusterMethod = ReflectionUtils.findMethod(RedisProperties.class, "getCluster");
    Method timeoutMethod = ReflectionUtils.findMethod(RedisProperties.class, "getTimeout");
    Object timeoutValue = null;
    if (null != timeoutMethod) {
      timeoutValue = ReflectionUtils.invokeMethod(timeoutMethod, this.redisProperties);
    }
    int timeout = 10000;
    if (null != timeoutValue) {
      if (timeoutValue instanceof Integer) {
        timeout = ((Integer)timeoutValue).intValue();
      } else {
        Method millisMethod = ReflectionUtils.findMethod(timeoutValue.getClass(), "toMillis");
        Long longTimeOut = null;
        if (null != millisMethod)
          longTimeOut = (Long)ReflectionUtils.invokeMethod(millisMethod, timeoutValue); 
        if (null != longTimeOut)
          timeout = longTimeOut.intValue(); 
      }
    }
    if (this.redissonProperties.getConfig() != null) {
      try {
        InputStream is = getConfigStream();
        config = Config.fromJSON(is);
      } catch (IOException e) {
        try {
          InputStream is = getConfigStream();
          config = Config.fromYAML(is);
        } catch (IOException e1) {
          throw new IllegalArgumentException("Can't parse config", e1);
        } 
      } 
    } else if (this.redisProperties.getSentinel() != null) {
      Method nodesMethod = ReflectionUtils.findMethod(RedisProperties.Sentinel.class, "getNodes");
      if (nodesMethod != null) {
        Object nodesValue = ReflectionUtils.invokeMethod(nodesMethod, this.redisProperties.getSentinel());
        String[] nodes = new String[0];
        if (nodesValue instanceof String) {
          nodes = convert(Arrays.asList(((String)nodesValue).split(",")));
        } else if (nodesValue instanceof List) {
          nodes = convert((List<String>)nodesValue);
        } 
        config = new Config();
        config.useSentinelServers()
          .setMasterName(this.redisProperties.getSentinel().getMaster())
          .addSentinelAddress(nodes)
          .setDatabase(this.redisProperties.getDatabase())
          .setTimeout(timeout)
          .setPassword(this.redisProperties.getPassword());
      } 
    } else if (clusterMethod != null && ReflectionUtils.invokeMethod(clusterMethod, this.redisProperties) != null) {
      Object clusterObject = ReflectionUtils.invokeMethod(clusterMethod, this.redisProperties);
      Method nodesMethod = ReflectionUtils.findMethod(clusterObject.getClass(), "getNodes");
      config = new Config();
      if (nodesMethod != null) {
        List<String> nodesObject = (List<String>)ReflectionUtils.invokeMethod(nodesMethod, clusterObject);
        String[] nodes = convert(nodesObject);
        config.useClusterServers()
          .addNodeAddress(nodes)
          .setTimeout(timeout)
          .setPassword(this.redisProperties.getPassword());
      } 
    } else {
      config = new Config();
      String prefix = "redis://";
      Method method = ReflectionUtils.findMethod(RedisProperties.class, "isSsl");
      if (null != method) {
        Object isSsl = ReflectionUtils.invokeMethod(method, this.redisProperties);
        if (null != isSsl && (
          (Boolean)isSsl).booleanValue())
          prefix = "rediss://"; 
      } 
      config.useSingleServer()
        .setAddress(prefix + this.redisProperties.getHost() + ":" + this.redisProperties.getPort())
        .setTimeout(timeout)
        .setDatabase(this.redisProperties.getDatabase())
        .setPassword(this.redisProperties.getPassword());
    } 
    return Redisson.create(config);
  }
  
  private InputStream getConfigStream() throws IOException {
    Resource resource = this.ctx.getResource(this.redissonProperties.getConfig());
    return resource.getInputStream();
  }
}
