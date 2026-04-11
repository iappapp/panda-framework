package com.github.iappapp.panda.common.annotation;

import lombok.Getter;

@Getter
public enum CryptAlgorithm {
    RSA(0),
    AES(1),
    ;

    private int code;

    CryptAlgorithm(int code) {
        this.code = code;
    }

    /**
     *
     * @param code
     * @return
     */
    public static CryptAlgorithm getEnumByCode(int code) {
        for (CryptAlgorithm algorithm : values()) {
            if (algorithm.getCode() == code) {
                return algorithm;
            }
        }
        return null;
    }
}
