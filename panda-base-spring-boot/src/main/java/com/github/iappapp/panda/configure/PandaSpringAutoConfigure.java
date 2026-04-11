package com.github.iappapp.panda.configure;

import com.github.iappapp.panda.controlleradvice.PandaLogResponseBodyAdvice;
import com.github.iappapp.panda.context.ApplicationContextHelper;
import com.github.iappapp.panda.controlleradvice.PandaResponseBodyAdvice;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author iappapp
 * @date 2025/6/26
 * @description TODO
 */
@Configuration
@ComponentScan(basePackageClasses = {
        ApplicationContextHelper.class,
        PandaFeignConfigure.class,
        PandaResponseBodyAdvice.class,
        PandaLogResponseBodyAdvice.class
})
public class PandaSpringAutoConfigure {
}
