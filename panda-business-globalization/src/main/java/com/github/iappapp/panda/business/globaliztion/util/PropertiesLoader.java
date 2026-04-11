/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.dahua.panda.base.core.code.IErrorCode
 *  com.dahua.panda.base.core.exception.BusinessException
 *  org.apache.commons.lang.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.ClassPathResource
 *  org.springframework.core.io.FileSystemResource
 *  org.springframework.core.io.Resource
 *  org.springframework.util.ResourceUtils
 */
package com.github.iappapp.panda.business.globaliztion.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.github.iappapp.panda.business.globaliztion.constant.GlobalizationErrorCodeEnum;
import com.github.iappapp.panda.exception.BusinessException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.ResourceUtils;

public class PropertiesLoader {
    private static final Logger log = LoggerFactory.getLogger(PropertiesLoader.class);
    private String configFilePath;
    private static Properties properties;
    private static Resource resource;

    public PropertiesLoader(String configFilePath) throws BusinessException {
        if (StringUtils.isEmpty(configFilePath)) {
            throw new BusinessException( GlobalizationErrorCodeEnum.PROPERTY_CONFIG_FILEPATH_IS_INVALID);
        }
        this.configFilePath = configFilePath;
        properties = this.loadProperties();
    }

    public Properties getProperties() {
        if (properties == null || properties.isEmpty()) {
            properties = this.loadProperties();
        }
        return properties;
    }

    public Properties loadProperties() {
        Properties props = new Properties();
        BufferedReader bufferedReader = null;
        try {
            if (this.configFilePath.startsWith("file:")) {
                this.configFilePath = this.configFilePath.replace("file:", "");
                resource = new FileSystemResource(this.configFilePath);
                props.load(resource.getInputStream());
            } else if (this.configFilePath.startsWith("classpath:")) {
                this.configFilePath = this.configFilePath.replace("classpath:", "");
                ClassPathResource resource = new ClassPathResource(this.configFilePath);
                bufferedReader = new BufferedReader(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
                props.load(bufferedReader);
            } else {
                File configFile = ResourceUtils.getFile(this.configFilePath);
                bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
                props.load(bufferedReader);
            }
            log.info("load config file conpleted: {}", this.configFilePath);
        }
        catch (Exception e) {
            log.error("load config file failed: {}", this.configFilePath);
        }
        finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                }
                catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return props;
    }

    public String getProperty(String key) {
        if (StringUtils.isEmpty(key)) {
            return "";
        }
        return String.valueOf(this.getProperties().get(key));
    }

    public boolean updateProperty(String key, String value) {
        HashMap<String, String> newProperties = new HashMap<String, String>();
        newProperties.put(key, value);
        boolean result = this.updateProperties(newProperties);
        return result;
    }

    public synchronized boolean updateProperties(Map<String, String> newPropertiesMap) {
        if (newPropertiesMap == null || newPropertiesMap.size() == 0) {
            return false;
        }
        Properties props = this.getProperties();
        BufferedWriter bufferedWriter = null;
        boolean result = false;
        try {
            File configFile = ResourceUtils.getFile(this.configFilePath);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile)));
            for (String key : newPropertiesMap.keySet()) {
                props.put(key, newPropertiesMap.get(key));
            }
            props.store(bufferedWriter, "generate by inner service, cannot be edited");
            properties = props;
            result = true;
        }
        catch (Exception e) {
            result = false;
            log.error(e.getMessage(), e);
        }
        finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.flush();
                    bufferedWriter.close();
                }
                catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return result;
    }

    static {
        resource = null;
    }
}

