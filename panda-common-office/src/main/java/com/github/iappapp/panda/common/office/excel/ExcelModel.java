/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.alibaba.excel.annotation.ExcelIgnore
 */
package com.github.iappapp.panda.common.office.excel;

import com.alibaba.excel.annotation.ExcelIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ExcelModel {
    @ExcelIgnore
    private String errorMessage = null;
}

