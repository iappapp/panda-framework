package com.github.iappapp.panda.common.encrypt;

import cn.hutool.core.util.ClassUtil;
import com.github.iappapp.panda.common.annotation.Encrypted;
import com.github.iappapp.panda.common.utils.IdCardUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * @author iappapp
 * @date 2025-09-08
 *
 * 数据加解密的拦截器
 */
@Intercepts({@Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})})
public class SecuredUpdateInterceptor extends SecuredBaseInterceptor implements Interceptor {
    private final Logger logger = LoggerFactory.getLogger(SecuredUpdateInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();

        List<OriginValueHolder> originValueHolderList = doEncrypt(args);
        try {
            return invocation.proceed();
        } finally {
            // 恢复被加密过的字段
            restore(originValueHolderList);
        }
    }

    private List<OriginValueHolder> doEncrypt(Object[] args) {
        List<OriginValueHolder> originValueHolderList = new ArrayList<>(args.length);

        for (Object arg : args) {
            try {
                encryptParameterObject(arg, originValueHolderList);
            } catch (Exception exception) {
                logger.warn("数据加密异常{}", exception.getMessage());
            }
        }

        return originValueHolderList;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }

    /**
     * 递归处理参数对象
     */
    private void encryptParameterObject(Object paramObj, List<OriginValueHolder> originValues)
            throws IllegalAccessException {
        if (paramObj == null || isBasicType(paramObj) || paramObj instanceof MappedStatement) {
            // 基础类型直接跳过
            return;
        }

        if (paramObj instanceof Map) {
            for (Object entry : ((Map) paramObj).entrySet()) {
                Object key = ((Map.Entry)entry).getKey();
                if (key instanceof String) {
                    String keyStr = (String) ((Map.Entry)entry).getKey();
                    // 跳过 MyBatis 自动生成的 param1、param2 参数
                    // 导致重复加密 需要跳过
                    if (keyStr.matches("^param\\d+$")) {
                        continue;
                    }
                }
                encryptParameterObject(((Map.Entry) entry).getValue(), originValues);
            }
            return;
        }

        if (paramObj instanceof Iterable) {
            for (Object element : (Iterable) paramObj) {
                encryptParameterObject(element, originValues);
            }
            return;
        }

        // 对象：扫描字段
        Field[] fields = getDeclaredField(paramObj);
        for (Field field : fields) {
            Encrypted encrypted = field.getAnnotation(Encrypted.class);
            if (encrypted == null) {
                continue;
            }

            field.setAccessible(true);
            Object fieldVal = field.get(paramObj);
            if (fieldVal instanceof String && !StringUtils.isEmpty(fieldVal)) {
                if (IdCardUtils.isEncrypted((String) fieldVal)) {
                    logger.info("field val encrypted {}", fieldVal);
                    continue;
                }
                originValues.add(new OriginValueHolder(paramObj, field, fieldVal));
                try {
                    SpringEncryptUtils encryptUtils = SpringContextHolder.getBean(SpringEncryptUtils.class);
                    String encryptedVal = encryptUtils.encrypt((String) fieldVal);
                    field.set(paramObj, encryptedVal);
                } catch (Exception e) {
                    logger.warn("Encrypt field {} failed", field.getName(), e);
                }
            }
        }
    }

    /**
     * 判断是否基础类型
     */
    private boolean isBasicType(Object obj) {
        return obj == null || ClassUtil.isSimpleValueType(obj.getClass());
    }
}
