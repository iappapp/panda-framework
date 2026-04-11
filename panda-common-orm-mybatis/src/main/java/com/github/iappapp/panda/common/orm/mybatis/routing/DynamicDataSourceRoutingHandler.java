/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.dynamic.datasource.DynamicDataSourceCreator
 *  com.baomidou.dynamic.datasource.DynamicRoutingDataSource
 *  com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 *  org.springframework.util.StringUtils
 */
package com.github.iappapp.panda.common.orm.mybatis.routing;

import com.baomidou.dynamic.datasource.DynamicDataSourceCreator;
import com.baomidou.dynamic.datasource.DynamicRoutingDataSource;
import java.util.Map;
import javax.sql.DataSource;

import com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DataSourceProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(prefix = "spring.datasource.dynamic", name = "primary", matchIfMissing = false)
public class DynamicDataSourceRoutingHandler {
    private static final Logger log = LoggerFactory.getLogger(DynamicDataSourceRoutingHandler.class);
    @Autowired
    private DataSource dataSource;
    @Autowired
    private DynamicDataSourceCreator dataSourceCreator;

    public Boolean addDataSource(DataSourceProperty dataSourceBean) {
        if (dataSourceBean == null
                || StringUtils.isEmpty(dataSourceBean.getPollName())
                || StringUtils.isEmpty(dataSourceBean.getUsername())
                || StringUtils.isEmpty(dataSourceBean.getUrl())
                || StringUtils.isEmpty(dataSourceBean.getPassword())
                || StringUtils.isEmpty(dataSourceBean.getDriverClassName())) {
            log.error("add dataSource,DataSourceProperty is empty");
            return false;
        }
        DataSourceProperty dataSourceProperty = dataSourceBean;
        try {
            DynamicRoutingDataSource ds = (DynamicRoutingDataSource)this.dataSource;
            DataSource dataSource = this.dataSourceCreator.createDataSource(dataSourceProperty);
            ds.addDataSource(dataSourceProperty.getPollName(), dataSource);
            return true;
        }
        catch (Exception e) {
            log.error("dataSourceCreator.createDataSource is error the dataSource ={}", dataSourceBean, e);
            return false;
        }
    }

    public void removeDataSource(String pollName) throws Exception {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource)this.dataSource;
        ds.removeDataSource(pollName);
    }

    public Map<String, DataSource> getDataSources() {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource)this.dataSource;
        return ds.getCurrentDataSources();
    }

    public DataSource getDataSourceByName(String pollName) {
        DynamicRoutingDataSource ds = (DynamicRoutingDataSource)this.dataSource;
        return ds.getCurrentDataSources().get(pollName);
    }
}

