/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.validation.Valid
 */
package com.github.iappapp.panda.common;

import lombok.Data;

import javax.validation.Valid;
import java.util.List;

@Data
public class PageReqVO<T> extends BasePageReqVO {
    @Valid
    private T condition;
    @Valid
    private T keyCondition;

    private List<OrderItem> order;
}

