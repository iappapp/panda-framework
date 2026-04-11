/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.dahua.panda.base.core.code.IErrorCode
 */
package com.github.iappapp.panda.business.globaliztion.constant;

import com.github.iappapp.panda.code.IErrorCode;

public enum GlobalizationErrorCodeEnum implements IErrorCode
{
    PROPERTY_CONFIG_FILEPATH_IS_INVALID("99001902", "902", "Failed to create instance of PropertiesHelper, the property 'configFilePath' is invalid or null", "国际化文件路径有误"),
    DIC_SEARCH_IS_TOO_LONG("99001903", "903", "There are too many entries in the batch query dictionary, supporting up to 1000 entries", "批量查询字典词条过多，最多支持1000条");

    private String saasErrorCode;
    private String originalCode;
    private String enDesc;
    private String zhDesc;

    public static GlobalizationErrorCodeEnum getEnumByOriginalCode(String originalCode) {
        GlobalizationErrorCodeEnum[] objArr;
        for (GlobalizationErrorCodeEnum anObjArr : objArr = GlobalizationErrorCodeEnum.values()) {
            if (originalCode.compareTo(anObjArr.originalCode) != 0) continue;
            return anObjArr;
        }
        return null;
    }

    public String getCode() {
        return this.saasErrorCode;
    }

    public String getMessage() {
        return this.enDesc;
    }

    public String getSaasErrorCode() {
        return this.saasErrorCode;
    }

    public String getOriginalCode() {
        return this.originalCode;
    }

    public String getEnDesc() {
        return this.enDesc;
    }

    public String getZhDesc() {
        return this.zhDesc;
    }

    private GlobalizationErrorCodeEnum(String saasErrorCode, String originalCode, String enDesc, String zhDesc) {
        this.saasErrorCode = saasErrorCode;
        this.originalCode = originalCode;
        this.enDesc = enDesc;
        this.zhDesc = zhDesc;
    }
}

