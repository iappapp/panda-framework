package com.github.iappapp.panda.common.encrypt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * SpringContextHolder - 将 Spring 的 ApplicationContext 存到静态变量中，方便在任何地方获取 Spring Bean。
 *
 * 使用方式：
 *   MyService svc = SpringContextHolder.getBean(MyService.class);
 *   MyService svc = SpringContextHolder.getBean("myService");
 *
 * 注意：
 * - 仅在确认 Spring 容器已启动并设置了此类为 bean 后才安全调用静态方法。
 * - 生产环境请谨慎滥用全局静态访问，优先使用依赖注入。
 */
@Component
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
    private static final Logger logger = LoggerFactory.getLogger(SpringContextHolder.class);

    /**
     * 持有 ApplicationContext 的静态变量，volatile 保证可见性
     */
    private static volatile ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext == null) {
            return;
        }
        // 仅在尚未设置或被清理后再次设置
        if (SpringContextHolder.context != null) {
            logger.warn("SpringContextHolder中的ApplicationContext被覆盖，原来的ApplicationContext将被替换。");
        }
        SpringContextHolder.context = applicationContext;
        logger.info("ApplicationContext 已注入 SpringContextHolder.");
    }

    /**
     * 获取 ApplicationContext（可能为 null）
     */
    @Nullable
    public static ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * 获取 Bean（按类型）
     *
     * @param requiredType bean 类型
     * @param <T>          泛型
     * @return bean 实例（如果不存在则抛出异常）
     */
    public static <T> T getBean(Class<T> requiredType) {
        ApplicationContext ctx = getContextOrThrow();
        return ctx.getBean(requiredType);
    }

    /**
     * 获取 Bean（按 name 强转）
     *
     * @param name bean 名称
     * @param <T>  泛型
     * @return bean 实例（调用方自行强转）
     */
    @SuppressWarnings("unchecked")
    public static <T> T getBean(String name) {
        ApplicationContext ctx = getContextOrThrow();
        return (T) ctx.getBean(name);
    }

    /**
     * 获取 Bean（按 name + 类型）
     */
    public static <T> T getBean(String name, Class<T> requiredType) {
        ApplicationContext ctx = getContextOrThrow();
        return ctx.getBean(name, requiredType);
    }

    /**
     * 根据类型获取所有同类型的 Bean（name -> instance）
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        ApplicationContext ctx = getContextOrThrow();
        return ctx.getBeansOfType(type);
    }

    /**
     * 判断容器中是否包含指定名称的 Bean
     */
    public static boolean containsBean(String name) {
        ApplicationContext ctx = getContextOrThrow();
        return ctx.containsBean(name);
    }

    /**
     * 判断给定 name 的 Bean 是否为单例
     */
    public static boolean isSingleton(String name) {
        ApplicationContext ctx = getContextOrThrow();
        return ctx.isSingleton(name);
    }

    /**
     * 获取 Bean 的类型
     */
    @Nullable
    public static Class<?> getType(String name) {
        ApplicationContext ctx = getContextOrThrow();
        return ctx.getType(name);
    }

    /**
     * 清理静态变量，通常在容器关闭时调用（由 Spring 的 DisposableBean 接口触发）
     */
    public static void clear() {
        context = null;
        logger.info("SpringContextHolder 清理 ApplicationContext.");
    }

    @Override
    public void destroy() throws Exception {
        clear();
    }

    /**
     * 获取上下文，若为 null 则抛出有说明性的异常，避免 NPE 并快速定位问题
     */
    private static ApplicationContext getContextOrThrow() {
        ApplicationContext ctx = SpringContextHolder.context;
        if (Objects.isNull(ctx)) {
            throw new IllegalStateException("ApplicationContext 未注入，请确保 SpringContextHolder 已被 Spring 管理并且容器已启动。");
        }
        return ctx;
    }
}
