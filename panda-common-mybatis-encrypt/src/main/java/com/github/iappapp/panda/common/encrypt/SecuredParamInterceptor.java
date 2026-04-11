package com.github.iappapp.panda.common.encrypt;

import cn.hutool.core.util.ClassUtil;
import com.github.iappapp.panda.common.annotation.Encrypted;
import com.github.iappapp.panda.common.utils.IdCardUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author iappapp
 * @date 2025-09-08
 *
 * 注解字段参数加密
 * 处理@Param注解参数加密，需要进行加密处理进行数据库查询
 */
@Intercepts({
        @Signature(type = ParameterHandler.class, method = "setParameters", args = {PreparedStatement.class})
})
@Slf4j
public class SecuredParamInterceptor extends SecuredBaseInterceptor implements Interceptor {
    private static final String PAGE_HELPER_COUNT = "_COUNT";

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        ParameterHandler parameterHandler = (ParameterHandler) invocation.getTarget();
        MetaObject metaObject = SystemMetaObject.forObject(parameterHandler);
        Object parameterObject = metaObject.getValue("parameterObject");

        List<OriginValueHolder> holderList = Lists.newArrayList();

        if (parameterObject instanceof MapperMethod.ParamMap) {

            MapperMethod.ParamMap<Object> paramMap = (MapperMethod.ParamMap<Object>) parameterObject;

            // 获取 Mapper 方法对应的参数注解
            MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("mappedStatement");
            String id = mappedStatement.getId();
            String className = id.substring(0, id.lastIndexOf("."));
            String methodName = id.substring(id.lastIndexOf(".") + 1);

            // PageHelp 统计sql执行兼容处理
            if (methodName.endsWith(PAGE_HELPER_COUNT)) {
                methodName = methodName.substring(0, methodName.lastIndexOf(PAGE_HELPER_COUNT));
            }

            // 支持@Param 添加相应注解自动加密 减少冗余代码
            Class<?> mapperClass = Class.forName(className);
            for (Method method : mapperClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    Annotation[][] paramAnnotations = method.getParameterAnnotations();
                    String[] paramNames = Arrays.stream(method.getParameters())
                            .map(p -> p.getAnnotation(Param.class))
                            .map(p -> p != null ? p.value() : null)
                            .toArray(String[]::new);

                    for (int i = 0; i < paramAnnotations.length; i++) {
                        String paramName = paramNames[i];
                        Object argValue = paramMap.get(paramName);

                        for (Annotation annotation : paramAnnotations[i]) {
                            if (annotation instanceof Encrypted) {
                                Object plainText = paramMap.get(paramName);
                                if (plainText instanceof String && StringUtils.isNoneEmpty((String) plainText)) {
                                    if (IdCardUtils.isEncrypted((String) plainText)) {
                                        log.info("field value encrypted {}", plainText);
                                        continue;
                                    }
                                    SpringEncryptUtils encryptUtils = SpringContextHolder.getBean(SpringEncryptUtils.class);
                                    String encrypted = encryptUtils.encrypt((String) plainText);
                                    paramMap.put(paramName, encrypted);
                                }
                            }
                        }

                        // 处理对象字段上的 @Encrypted
                        if (argValue != null && !(argValue instanceof String)
                                && !ClassUtil.isSimpleValueType(argValue.getClass())) {
                            handleObjectFields(argValue, holderList);
                        }
                    }
                }
            }
        }
        try {
            return invocation.proceed();
        } finally {
            restore(holderList);
        }
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 扫描对象属性上的 @Encrypted 注解，进行加密
     * 暂时仅支持单个对象查询 不支持list map等集合嵌套数据
     */
    private void handleObjectFields(Object obj, List<OriginValueHolder> holderList) throws IllegalAccessException {


        Field[] fields = getDeclaredField(obj);

        for (Field field : fields) {
            if (field.isAnnotationPresent(Encrypted.class)) {
                field.setAccessible(true);
                Object value = field.get(obj);

                if (value instanceof String && StringUtils.isNotEmpty((String) value)) {
                    SpringEncryptUtils encryptUtils = SpringContextHolder.getBean(SpringEncryptUtils.class);
                    if (IdCardUtils.isEncrypted((String) value)) {
                        log.info("field value encrypted {}", value);
                        continue;
                    }
                    holderList.add(new OriginValueHolder(obj, field, value));
                    String encrypted = encryptUtils.encrypt((String) value);
                    field.set(obj, encrypted);
                }
            }
        }
    }
}

