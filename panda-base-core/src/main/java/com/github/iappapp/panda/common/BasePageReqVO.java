/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.common;

import lombok.Data;

@Data
public class BasePageReqVO {
    private Integer page;
    private Integer pageSize;

    public void normalize(Integer defaultPage, Integer defaultPageSize, Integer maxPageSize) {
        if (this.page == null || this.page < 0) {
            this.page = defaultPage;
        }
        if (this.pageSize == null || this.pageSize < 0) {
            this.pageSize = defaultPageSize;
        }
        if (this.pageSize > maxPageSize) {
            this.pageSize = maxPageSize;
        }
    }
}

