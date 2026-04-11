package com.github.iappapp.panda.business.resource;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;


@Slf4j
public class ResourceReportService implements SmartInitializingSingleton {
    @Autowired(required = false)
    private NacosConfigProperties nacosConfigProperties;

    @Value("${spring.application.name:}")
    private String applicationName;

    private static final String ROUTE_JSON_FILE = "route.json";

    @Autowired(required = false)
    private NacosConfigManager nacosConfigManager;

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void afterSingletonsInstantiated() {
        if (Objects.isNull(nacosConfigProperties)) {
            log.info("nacos config properties is not set in environment");
            return;
        }

        if (Objects.isNull(nacosConfigManager)) {
            log.info("nacos manager is not set in environment");
            return;
        }

        if (StringUtils.isEmpty(applicationName)) {
            log.info("resource report application name is empty");
            return;
        }

        String dataId = StringUtils.join(Lists.newArrayList(applicationName, ROUTE_JSON_FILE), ":");

        Resource resource = resourceLoader.getResource("classpath:" + ROUTE_JSON_FILE);
        try {
            String content = IOUtils.toString(resource.getInputStream(), StandardCharsets.UTF_8);
            JSONArray jsonArray = JSON.parseArray(content);
            for (Object jsonObject : jsonArray) {
                ((JSONObject) jsonObject).put("timestamp", new Date().getTime());
            }
            content = JSON.toJSONString(jsonArray, true);
            log.info("resource report group {} dataId {} content {}", nacosConfigProperties.getGroup(), dataId, content);
            nacosConfigManager.getConfigService().publishConfig(dataId, nacosConfigProperties.getGroup(), content);
            resource.getInputStream().close();
        } catch (NacosException ex) {
            log.error("resource report nacos error", ex);
        } catch (IOException ex) {
            log.error("resource report io error", ex);
        }
    }

}
