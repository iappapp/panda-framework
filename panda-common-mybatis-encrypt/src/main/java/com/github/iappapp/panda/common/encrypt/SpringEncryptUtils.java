package com.github.iappapp.panda.common.encrypt;

import com.github.iappapp.panda.common.utils.EncryptUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author iappapp
 * @date 2025-09-08
 */
@Component
public class SpringEncryptUtils {

    @Value("${panda.common.mybatis.encrypt.aesKey:}")
    private String aesSecretKey;

    public String decrypt(String encryptText) {
        return EncryptUtils.decrypt(encryptText, aesSecretKey);
    }

    public String encrypt(String plainText) {
        return EncryptUtils.encrypt(plainText, aesSecretKey);
    }
}
