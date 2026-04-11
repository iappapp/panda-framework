/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.common.task.quartz;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value={ElementType.TYPE})
@Retention(value=RetentionPolicy.RUNTIME)
public @interface AutoLoadJob {
    boolean retry() default true;
}

