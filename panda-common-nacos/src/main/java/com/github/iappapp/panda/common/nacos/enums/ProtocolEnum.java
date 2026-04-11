/*
 * Decompiled with CFR 0.152.
 */
package com.github.iappapp.panda.common.nacos.enums;

public enum ProtocolEnum {
    HTTP("http://"),
    HTTPS("https://");

    private String protocol;

    ProtocolEnum(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol() {
        return this.protocol;
    }
}

