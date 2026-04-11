package com.github.iappapp.panda.common.job.autoconfigure;

import com.github.iappapp.panda.common.job.biz.client.PandaJobClient;
import com.github.iappapp.panda.common.job.config.PandaJobProperties;
import com.github.iappapp.panda.common.job.constant.Constant;
import com.github.iappapp.panda.common.job.executor.impl.PandaJobSpringExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableConfigurationProperties({PandaJobProperties.class})
public class PandaJobAutoConfiguration {
    private static final Logger log = LoggerFactory.getLogger(PandaJobAutoConfiguration.class);


    @Bean(name = "pandaJobSpringExecutor")
    @ConditionalOnMissingBean({PandaJobSpringExecutor.class})
    @ConditionalOnProperty(prefix = "panda.job", name = "adminAddresses", matchIfMissing = false)
    public PandaJobSpringExecutor pandaJobExecutor(PandaJobProperties properties) {
        PandaJobSpringExecutor executor = new PandaJobSpringExecutor();
        List<String> adminAddress = new ArrayList<>(2);
        if (!properties.getAdminAddresses().contains(Constant.HTTP) &&
                !properties.getAdminAddresses().contains(Constant.HTTPS)) {
            String[] addressArr = properties.getAdminAddresses().split(",");
            for (String ip : addressArr) {
                adminAddress.add(String.format("http://%s:38388/g-job", ip));
            }
            properties.setAdminAddresses(StringUtils.collectionToDelimitedString(adminAddress, ","));
            executor.setAdminAddresses(properties.getAdminAddresses());
        } else {
            executor.setAdminAddresses(properties.getAdminAddresses());
        }
        executor.setAppname(properties.getAppname());
        executor.setAddress(properties.getAddress());
        executor.setIp(properties.getIp());
        executor.setPort(properties.getPort());
        executor.setAccessToken(properties.getAccessToken());
        executor.setLogPath(properties.getLogPath());
        executor.setLogRetentionDays(properties.getLogRetentionDays());
        executor.setUsername(properties.getUserName());
        executor.setPassword(properties.getPassword());
        return executor;
    }

    @Bean(name = "pandaJobClient")
    @ConditionalOnBean({PandaJobSpringExecutor.class})
    public PandaJobClient pandaJobClient() {
        log.info("panda-job init pandaJobClient");
        return new PandaJobClient();
    }
}
