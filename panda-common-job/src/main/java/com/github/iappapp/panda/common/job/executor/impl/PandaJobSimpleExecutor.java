package com.github.iappapp.panda.common.job.executor.impl;

import com.github.iappapp.panda.common.job.executor.PandaJobExecutor;
import com.github.iappapp.panda.common.job.handler.annotation.PandaJob;
import com.github.iappapp.panda.common.job.handler.impl.MethodJobHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * panda-job executor (for frameless)
 *
 * @author xuxueli 2020-11-05
 */
public class PandaJobSimpleExecutor extends PandaJobExecutor {
    private static final Logger logger = LoggerFactory.getLogger(PandaJobSimpleExecutor.class);


    private List<Object> jobBeanList = new ArrayList<>();

    public List<Object> getJobBeanList() {
        return jobBeanList;
    }

    public void setJobBeanList(List<Object> jobBeanList) {
        this.jobBeanList = jobBeanList;
    }


    @Override
    public void start() {

        // init JobHandler Repository (for method)
        initJobHandlerMethodRepository(jobBeanList);

        // super start
        try {
            super.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }


    private void initJobHandlerMethodRepository(List<Object> xxlJobBeanList) {
        if (xxlJobBeanList == null || xxlJobBeanList.size() == 0) {
            return;
        }

        // init job handler from method
        for (Object bean : xxlJobBeanList) {
            // method
            Method[] methods = bean.getClass().getDeclaredMethods();
            if (methods == null || methods.length == 0) {
                continue;
            }
            for (Method executeMethod : methods) {

                // anno
                PandaJob pandaJob = executeMethod.getAnnotation(PandaJob.class);
                if (pandaJob == null) {
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

                // init and destory
                Method initMethod = null;
                Method destroyMethod = null;

                if (pandaJob.init().trim().length() > 0) {
                    try {
                        initMethod = bean.getClass().getDeclaredMethod(pandaJob.init());
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

                // registry jobhandler
                registJobHandler(name, new MethodJobHandler(bean, executeMethod, initMethod, destroyMethod));

            }

        }

    }
}
