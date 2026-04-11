//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.github.iappapp.panda.common.encrypt;

import cn.hutool.core.map.MapUtil;
import com.github.pagehelper.autoconfigure.PageHelperAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * @author iappapp
 * @date 2025-09-08
 *
 * 自定义插件配置类
 */
@Configuration
@AutoConfigureAfter({PageHelperAutoConfiguration.class})
@Slf4j
public class MybatisInterceptorAutoConfiguration {
    @Value("#{${panda.common.mybatis.encrypt.exclude:}.split(',')}")
    private List<String> sqlSessionFactoryExcludes;

    @Autowired
    private ApplicationContext applicationContext;

    public MybatisInterceptorAutoConfiguration() {
    }

    @PostConstruct
    public void addMybatisInterceptor() {
        Map<String, SqlSessionFactory> stringSqlSessionFactoryMap = applicationContext.getBeansOfType(SqlSessionFactory.class);
        if (MapUtil.isEmpty(stringSqlSessionFactoryMap)) {
            log.info("sqlSessionFactory is empty");
            return;
        }
        for (Map.Entry<String, SqlSessionFactory> entry : stringSqlSessionFactoryMap.entrySet()) {
            SqlSessionFactory sqlSessionFactory = entry.getValue();
            String sqlSessionFactoryName = entry.getKey();
            if (sqlSessionFactoryExcludes.contains(sqlSessionFactoryName)) {
                log.info("sqlSessionFactory {} is excluded", sqlSessionFactoryName);
                continue;
            }
            sqlSessionFactory.getConfiguration().addInterceptor(new SecuredParamInterceptor());
            sqlSessionFactory.getConfiguration().addInterceptor(new SecuredUpdateInterceptor());
            sqlSessionFactory.getConfiguration().addInterceptor(new SecuredQueryInterceptor());
        }
    }
}
