package com.github.iappapp.panda.common.job.biz.model;

import java.io.Serializable;

public class RegistryParam implements Serializable {
    private static final long serialVersionUID = 42L;

    private String registryGroup;

    private String registryKey;

    private String registryValue;

    public RegistryParam() {
    }

    public RegistryParam(String registryGroup, String registryKey, String registryValue) {
        this.registryGroup = registryGroup;
        this.registryKey = registryKey;
        this.registryValue = registryValue;
    }

    public String getRegistryGroup() {
        return this.registryGroup;
    }

    public void setRegistryGroup(String registryGroup) {
        this.registryGroup = registryGroup;
    }

    public String getRegistryKey() {
        return this.registryKey;
    }

    public void setRegistryKey(String registryKey) {
        this.registryKey = registryKey;
    }

    public String getRegistryValue() {
        return this.registryValue;
    }

    public void setRegistryValue(String registryValue) {
        this.registryValue = registryValue;
    }

    public String toString() {
        return "RegistryParam{registryGroup='" + this.registryGroup + '\'' + ", registryKey='" + this.registryKey + '\'' + ", registryValue='" + this.registryValue + '\'' + '}';
    }
}
