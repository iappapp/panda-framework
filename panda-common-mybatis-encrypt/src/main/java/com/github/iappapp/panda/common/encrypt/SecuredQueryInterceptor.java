package com.github.iappapp.panda.common.encrypt;

import com.github.iappapp.panda.common.annotation.Encrypted;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;


/**
 * @author iappapp
 * @date 2025-09-08
 *
 * 字段加密插件
 */
@Intercepts({@Signature(type = ResultSetHandler.class, method = "handleResultSets", args = {Statement.class})})
public class SecuredQueryInterceptor extends SecuredBaseInterceptor implements Interceptor {
    private final Logger logger = LoggerFactory.getLogger(SecuredQueryInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object result = invocation.proceed();

        if (result instanceof List) {
            List<?> resultList = (List<?>) result;
            for (Object o : resultList) {
                // 不处理jdk中的类型
                if (null == o || o.getClass().getName().startsWith("java")) {
                    continue;
                }

                Field[] fields = getDeclaredField(o);// o.getClass().getDeclaredFields();
                for (Field field : fields) {
                    Encrypted encrypted = field.getAnnotation(Encrypted.class);
                    if (encrypted == null) {
                        continue;
                    }

                    field.setAccessible(true);
                    Object val = field.get(o);
                    if (val instanceof String) {
                        try {
                            SpringEncryptUtils encryptUtils = SpringContextHolder.getBean(SpringEncryptUtils.class);
                            field.set(o, encryptUtils.decrypt((String) val));
                        } catch (Exception ex) {
                            logger.error("解密失败", ex);
                        }
                    }
                }
            }

            return result;
        } else {
            logger.warn("unexpected result type for SecuredQueryInterceptor: {}", result.getClass());
        }

        return result;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
