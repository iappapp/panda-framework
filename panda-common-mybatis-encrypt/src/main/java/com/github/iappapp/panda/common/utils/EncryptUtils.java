/**
 * Aistarfish.com Inc.
 * Copyright (c) 2017-2025 All Rights Reserved.
 */
package com.github.iappapp.panda.common.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加解密工具类
 * @author qiyu
 * Created by on 2025-09-04 15:37
 */
public class EncryptUtils {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    // AES-128需要16字节密钥
    private static final String DEFAULT_KEY = "1234567890ABCDEF";

    /**
     * 加密
     * @param plainText 明文
     * @return 加密后的密文(Base64编码)
     */
    public static String encrypt(String plainText) {
        return encrypt(plainText, DEFAULT_KEY);
    }

    /**
     * 解密
     * @param encryptedText 密文(Base64编码)
     * @return 解密后的明文
     */
    public static String decrypt(String encryptedText) {
        return decrypt(encryptedText, DEFAULT_KEY);
    }

    /**
     * 使用指定密钥加密
     * @param plainText 明文
     * @param key 密钥(必须是16字节)
     * @return 加密后的密文(Base64编码)
     */
    public static String encrypt(String plainText, String key) {
        if (plainText == null) return null;
        if (key == null || key.length() != 16) {
            throw new IllegalArgumentException("密钥必须是16字节长度");
        }

        try {

            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("加密失败", e);
        }

    }

    /**
     * 使用指定密钥解密
     * @param encryptedText 密文(Base64编码)
     * @param key 密钥(必须是16字节)
     * @return 解密后的明文
     */
    public static String decrypt(String encryptedText, String key) {
        if (encryptedText == null) {
            return null;
        }
        if (encryptedText.length() <= 18) {
            return encryptedText;
        }
        if (key == null || key.length() != 16) {
            throw new IllegalArgumentException("密钥必须是16字节长度");
        }

        try {

            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, ALGORITHM);

            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);

            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("解密失败", e);
        }
    }
}
