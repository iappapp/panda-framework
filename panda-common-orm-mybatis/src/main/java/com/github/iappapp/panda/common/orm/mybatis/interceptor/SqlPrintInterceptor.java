package com.github.iappapp.panda.common.orm.mybatis.interceptor;

import com.github.iappapp.panda.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;

/**
 * @author tiger
 * @date 2025-06-25
 */
@Intercepts({
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
    @Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
})
@Slf4j
public class SqlPrintInterceptor implements Interceptor {

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
        Object param = invocation.getArgs()[1];
        BoundSql boundSql = ms.getBoundSql(param);
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        List<ParameterMapping> paramMappings = boundSql.getParameterMappings();
        Object paramObject = boundSql.getParameterObject();
        Configuration configuration = ms.getConfiguration();
        TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();

        // 替换参数
        for (ParameterMapping pm : paramMappings) {
            Object value;
            String propertyName = pm.getProperty();
            if (boundSql.hasAdditionalParameter(propertyName)) {
                value = boundSql.getAdditionalParameter(propertyName);
            } else if (Objects.isNull(paramObject)) {
                value = null;
            } else if (typeHandlerRegistry.hasTypeHandler(paramObject.getClass())) {
                value = paramObject;
            } else {
                MetaObject metaObject = configuration.newMetaObject(paramObject);
                value = metaObject.getValue(propertyName);
            }
            sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(value)));
        }

        log.info("origin full sql {}", sql);
        return invocation.proceed();
    }

    private String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj + "'";
        } else if (obj instanceof Date) {
            value = "'" + DateUtils.formatDateTime((Date) obj) + "'";
        } else {
            if (Objects.nonNull(obj)) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }
}
