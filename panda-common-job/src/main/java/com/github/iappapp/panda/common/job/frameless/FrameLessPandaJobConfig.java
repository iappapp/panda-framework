package com.github.iappapp.panda.common.job.frameless;

import com.github.iappapp.panda.common.job.executor.impl.PandaJobSimpleExecutor;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FrameLessPandaJobConfig {
    private static Logger logger = LoggerFactory.getLogger(FrameLessPandaJobConfig.class);

    private static FrameLessPandaJobConfig instance = new FrameLessPandaJobConfig();

    public static FrameLessPandaJobConfig getInstance() {
        return instance;
    }

    private PandaJobSimpleExecutor pandaJobExecutor = null;

    public void initPandaJobExecutor(List<Object> jobBeanList) {
        Properties pandaJobProp = loadProperties("panda-job-executor.properties");
        this.pandaJobExecutor = new PandaJobSimpleExecutor();
        this.pandaJobExecutor.setAdminAddresses(pandaJobProp.getProperty("panda.job.adminAddresses"));
        this.pandaJobExecutor.setAccessToken(pandaJobProp.getProperty("panda.job.accessToken"));
        this.pandaJobExecutor.setAppname(pandaJobProp.getProperty("panda.job.appname"));
        this.pandaJobExecutor.setAddress(pandaJobProp.getProperty("panda.job.address"));
        this.pandaJobExecutor.setIp(pandaJobProp.getProperty("panda.job.ip"));
        this.pandaJobExecutor.setPort(Integer.valueOf(pandaJobProp.getProperty("panda.job.port")));
        this.pandaJobExecutor.setLogPath(pandaJobProp.getProperty("panda.job.logpath"));
        this.pandaJobExecutor.setLogRetentionDays(Integer.valueOf(pandaJobProp.getProperty("panda.job.logretentiondays")));
        this.pandaJobExecutor.setJobBeanList(jobBeanList);
        try {
            this.pandaJobExecutor.start();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void destroyPandaJobExecutor() {
        if (this.pandaJobExecutor != null)
            this.pandaJobExecutor.destroy();
    }

    public static Properties loadProperties(String propertyFileName) {
        InputStreamReader in = null;
        try {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            in = new InputStreamReader(loader.getResourceAsStream(propertyFileName), "UTF-8");
            if (in != null) {
                Properties prop = new Properties();
                prop.load(in);
                return prop;
            }
        } catch (IOException e) {
            logger.error("load {} error!", propertyFileName);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    logger.error("close {} error!", propertyFileName);
                }
        }
        return null;
    }
}
