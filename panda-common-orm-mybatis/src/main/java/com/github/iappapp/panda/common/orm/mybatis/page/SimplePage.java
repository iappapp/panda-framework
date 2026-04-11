/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.baomidou.mybatisplus.extension.plugins.pagination.Page
 *  com.dahua.panda.base.core.model.PageRespVO
 */
package com.github.iappapp.panda.common.orm.mybatis.page;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.iappapp.panda.common.PageRespVO;

import java.util.ArrayList;
import java.util.List;

public class SimplePage<T> extends Page<T> {
    public <E> PageRespVO<E> toPageMsg(List<E> results) {
        PageRespVO respMsg = new PageRespVO();
        if (this.getCurrent() == 0L) {
            respMsg.setResults(new ArrayList());
        } else {
            respMsg.setResults(results);
        }
        boolean nextPageExist = this.getCurrent() == this.getPages()
                || this.getPages() == 0L
                || results == null
                || results.size() == 0;
        respMsg.setNextPage(Long.valueOf(nextPageExist ? -1L : this.getCurrent() + 1L).intValue());
        respMsg.setTotalCount((int) this.getTotal());
        return respMsg;
    }
}

