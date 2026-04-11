/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.core.metadata.OrderItem
 *  com.dahua.panda.base.core.exception.BusinessException
 *  com.dahua.panda.base.core.model.OrderItem
 *  com.dahua.panda.base.core.model.PageReqVO
 *  org.springframework.core.io.support.PropertiesLoaderUtils
 *  org.springframework.util.StringUtils
 */
package com.github.iappapp.panda.common.orm.mybatis.page;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import com.github.iappapp.panda.common.OrderItem;
import com.github.iappapp.panda.common.PageReqVO;
import com.github.iappapp.panda.exception.BusinessException;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.StringUtils;

public class PageHelper {
    private static final Integer MAX_PAGE_SIZE = 512;
    private static final Integer DEFAULT_PAGE_SIZE = 20;

    public static <T> SimplePage<T> toPage(PageReqVO<T> vo) {
        PageHelper.validate(vo);
        SimplePage page = new SimplePage();
        page.setCurrent(vo.getPage().intValue());
        page.setSize(vo.getPageSize().intValue());
        PageHelper.setOrderItem(page, vo.getOrder());
        return page;
    }

    private static <T> void validate(PageReqVO<T> vo) {
        if (vo.getPage() == null) {
            vo.setPage(Integer.valueOf(1));
        }
        if (vo.getPageSize() == null) {
            vo.setPageSize(DEFAULT_PAGE_SIZE);
        }
        if (vo.getPage() < 0) {
            throw new BusinessException("520", "page: field size needs to be greater than or equal to 0");
        }
        if (vo.getPageSize() < 0) {
            throw new BusinessException("520", "pageSize: field size needs to be greater than or equal to 0");
        }
        Properties properties = null;
        String isPageSizeLimited = null;
        try {
            properties = PropertiesLoaderUtils.loadAllProperties((String)"application.properties");
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if (properties != null) {
            isPageSizeLimited = properties.getProperty("page.size.limited");
        }
        if (StringUtils.isEmpty(isPageSizeLimited)) {
            isPageSizeLimited = "true";
        }
        if (vo.getPageSize() > MAX_PAGE_SIZE && Boolean.TRUE.toString().equals(isPageSizeLimited)) {
            vo.setPageSize(MAX_PAGE_SIZE);
        }
    }

    private static void setOrderItem(SimplePage page, List<OrderItem> items) {
        if (items == null || items.size() == 0) {
            return;
        }
        ArrayList<com.baomidou.mybatisplus.core.metadata.OrderItem> orderItems = new ArrayList<com.baomidou.mybatisplus.core.metadata.OrderItem>(items.size());
        for (OrderItem item : items) {
            com.baomidou.mybatisplus.core.metadata.OrderItem orderItem = new com.baomidou.mybatisplus.core.metadata.OrderItem();
            orderItem.setColumn(item.getOrderBy());
            if ("asc".equalsIgnoreCase(item.getOrder())) {
                orderItem.setAsc(true);
            } else {
                if (!"desc".equalsIgnoreCase(item.getOrder())) {
                    continue;
                }
                orderItem.setAsc(false);
            }
            orderItems.add(orderItem);
        }
        page.setOrders(orderItems);
    }

    public static <M, T> SimplePage<M> toPageOther(PageReqVO<T> vo) {
        SimplePage mSimplePage = new SimplePage();
        SimplePage<T> simplePage = PageHelper.toPage(vo);
        mSimplePage.setCurrent(simplePage.getCurrent());
        mSimplePage.setSize(simplePage.getSize());
        mSimplePage.setOrders(simplePage.getOrders());
        return mSimplePage;
    }
}

