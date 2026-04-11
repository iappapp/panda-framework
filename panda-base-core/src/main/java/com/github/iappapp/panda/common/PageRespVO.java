/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.common;

import lombok.Data;

import java.util.List;

@Data
public class PageRespVO<T> {
    private Integer totalCount;
    private Integer nextPage;
    private List<T> results;
}

