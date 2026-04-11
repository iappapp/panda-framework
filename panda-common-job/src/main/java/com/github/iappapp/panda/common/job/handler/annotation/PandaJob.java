package com.github.iappapp.panda.common.job.handler.annotation;


import com.github.iappapp.panda.common.job.enums.ExecutorBlockStrategyEnum;
import com.github.iappapp.panda.common.job.enums.ExecutorRouteStrategyEnum;
import com.github.iappapp.panda.common.job.enums.MisfireStrategyEnum;
import com.github.iappapp.panda.common.job.enums.ScheduleTypeEnum;
import com.github.iappapp.panda.common.job.enums.TriggerStatusEnum;
import com.github.iappapp.panda.common.job.glue.GlueTypeEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tiger
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface PandaJob {
    String value() default "";

    String jobDesc() default "";

    String init() default "";

    String destroy() default "";

    String author() default "";

    String alarmEmail() default "";

    ScheduleTypeEnum scheduleType() default ScheduleTypeEnum.CRON;

    String scheduleConf() default "";

    GlueTypeEnum glueType() default GlueTypeEnum.BEAN;

    String executorParam() default "";

    ExecutorRouteStrategyEnum executorRouteStrategy() default ExecutorRouteStrategyEnum.RANDOM;

    String childJobId() default "";

    TriggerStatusEnum triggerStatus() default TriggerStatusEnum.STATUS_CLOSE;

    MisfireStrategyEnum misfireStrategy() default MisfireStrategyEnum.DO_NOTHING;

    ExecutorBlockStrategyEnum executorBlockStrategy()
            default ExecutorBlockStrategyEnum.SERIAL_EXECUTION;

    int executorTimeout() default 0;

    int executorFailRetryCount() default 0;
}
