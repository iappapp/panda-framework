package com.github.iappapp.panda.common.job.executor.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.iappapp.panda.common.job.biz.model.JobInfoParam;
import com.github.iappapp.panda.common.job.config.PandaJobProperties;
import com.github.iappapp.panda.common.job.executor.PandaJobExecutor;
import com.github.iappapp.panda.common.job.glue.GlueFactory;
import com.github.iappapp.panda.common.job.handler.IJobHandler;
import com.github.iappapp.panda.common.job.handler.annotation.PandaJob;
import com.github.iappapp.panda.common.job.handler.impl.MethodJobHandler;
import com.github.iappapp.panda.common.job.thread.ExecutorCleanLogThread;
import com.github.iappapp.panda.common.job.thread.ExecutorRegistryGroupThread;
import com.github.iappapp.panda.common.job.thread.ExecutorRegistryJobThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.StringUtils;

public class PandaJobSpringExecutor extends PandaJobExecutor implements
        ApplicationContextAware, SmartInitializingSingleton, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(PandaJobSpringExecutor.class);

    @Autowired
    private PandaJobProperties pandaJobProperties;

    private List<JobInfoParam> jobList = new ArrayList<>(32);

    private static ApplicationContext applicationContext;

    @Override
    public void afterSingletonsInstantiated() {
        initJobHandlerMethodRepository(applicationContext);
        GlueFactory.refreshInstance(1);
        try {
            start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        initJobGroup(pandaJobProperties.getUserName(),
                pandaJobProperties.getPassword(), pandaJobProperties.getAppname());
        initJobInfo(pandaJobProperties.getUserName(),
                pandaJobProperties.getPassword(), pandaJobProperties.getAppname(), jobList);
        initCleanLogTask(pandaJobProperties.getUserName(),
                pandaJobProperties.getPassword(), pandaJobProperties.getAppname());
    }

    @Override
    public void destroy() {
        super.destroy();
    }

    public void initJobHandlerMethodRepository(ApplicationContext applicationContext) {
        if (applicationContext == null) {
            return;
        }
        String[] beanDefinitionNames = applicationContext.getBeanNamesForType(Object.class, false, true);
        for (String beanDefinitionName : beanDefinitionNames) {
            Object bean = applicationContext.getBean(beanDefinitionName);
            Map<Method, PandaJob> annotatedMethods = null;
            try {
                annotatedMethods = MethodIntrospector.selectMethods(bean.getClass(), new MethodIntrospector.MetadataLookup<PandaJob>() {
                    public PandaJob inspect(Method method) {
                        return AnnotatedElementUtils.findMergedAnnotation(method, PandaJob.class);
                    }
                });
            } catch (Throwable ex) {
                logger.error("panda-job method-jobhandler resolve error for bean[" + beanDefinitionName + "].", ex);
            }
            if (annotatedMethods != null && !annotatedMethods.isEmpty()) {
                for (Map.Entry<Method, PandaJob> methodJobEntry : annotatedMethods.entrySet()) {
                    Method executeMethod = methodJobEntry.getKey();
                    PandaJob pandaJob = methodJobEntry.getValue();
                    if (pandaJob == null)
                        continue;
                    if (StringUtils.isEmpty(pandaJob.value())) {
                        registJobHandler(executeMethod.getName(), new MethodJobHandler(bean, executeMethod, null, null));
                        continue;
                    }
                    String name = pandaJob.value();
                    if (name.trim().length() == 0) {
                        throw new RuntimeException("panda-job method-jobhandler name invalid, for[" + bean.getClass() + "#" + executeMethod.getName() + "] .");
                    }
                    if (loadJobHandler(name) != null) {
                        throw new RuntimeException("panda-job jobhandler[" + name + "] naming conflicts.");
                    }
                    executeMethod.setAccessible(true);
                    Method initMethod = null;
                    Method destroyMethod = null;
                    if (pandaJob.init().trim().length() > 0) {
                        try {
                            initMethod = bean.getClass().getDeclaredMethod(pandaJob.init(), new Class[0]);
                            initMethod.setAccessible(true);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException("panda-job method-jobhandler initMethod invalid, for[" + bean.getClass() + "#" + executeMethod.getName() + "] .");
                        }
                    }
                    if (pandaJob.destroy().trim().length() > 0) {
                        try {
                            destroyMethod = bean.getClass().getDeclaredMethod(pandaJob.destroy());
                            destroyMethod.setAccessible(true);
                        } catch (NoSuchMethodException e) {
                            throw new RuntimeException("panda-job method-jobhandler destroyMethod invalid, for[" + bean.getClass() + "#" + executeMethod.getName() + "] .");
                        }
                    }
                    registJobHandler(name, new MethodJobHandler(bean, executeMethod, initMethod, destroyMethod));
                    jobList.add(convertPandaJob(pandaJob));
                }
            }
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        PandaJobSpringExecutor.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    private void initJobGroup(String userName, String password, String appname) {
        ExecutorRegistryGroupThread.getInstance().start(appname, userName, password);
    }

    private void initJobInfo(String userName, String password, String appname, List<JobInfoParam> jobList) {
        ExecutorRegistryJobThread.getInstance().start(appname, userName, password, jobList);
    }

    private void initCleanLogTask(String userName, String password, String appname) {
        ExecutorCleanLogThread.getInstance().start(appname, userName, password);
    }

    public List<JobInfoParam> getJobList() {
        return this.jobList;
    }

    private JobInfoParam convertPandaJob(PandaJob pandaJob) {
        JobInfoParam param = new JobInfoParam();
        param.setJobDesc(pandaJob.jobDesc());
        param.setAuthor(pandaJob.author());
        param.setAlarmEmail(pandaJob.alarmEmail());
        param.setScheduleType(pandaJob.scheduleType().name());
        param.setScheduleConf(applicationContext.getEnvironment().resolvePlaceholders(pandaJob.scheduleConf()));
        param.setGlueType(pandaJob.glueType().name());
        param.setExecutorHandler(pandaJob.value());
        param.setExecutorParam(pandaJob.executorParam());
        param.setExecutorRouteStrategy(pandaJob.executorRouteStrategy().name());
        param.setChildJobId(pandaJob.childJobId());
        param.setMisfireStrategy(pandaJob.misfireStrategy().name());
        param.setExecutorBlockStrategy(pandaJob.executorBlockStrategy().name());
        param.setExecutorTimeout(pandaJob.executorTimeout());
        param.setExecutorFailRetryCount(pandaJob.executorFailRetryCount());
        param.setTriggerStatus(pandaJob.triggerStatus().getCode());
        return param;
    }
}
