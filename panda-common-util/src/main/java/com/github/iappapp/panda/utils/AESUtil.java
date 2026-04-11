package com.github.iappapp.panda.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * @author liush2
 */


public class AESUtil {

    private static final String KEY_ALGORITHM = "AES";
    // 默认的加密算法
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
    // 默认
    private static final String charset = "utf-8";

    public static String encrypt(String content, String key) {

        byte[] result = new byte[0];// 加密
        try {
            // 创建密码器
            Cipher cipher =Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);

            byte[] byteContent = content.getBytes(charset);

            // 初始化为加密模式的密码器
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));

            result = cipher.doFinal(byteContent);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | UnsupportedEncodingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        // 通过Base64转码返回
        return Base64.encodeBase64String(result);
    }

    public static String decrypt(String content, String key) {
        String clearText = null;
        try {
            // 实例化
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            // 使用密钥初始化，设置为解密模式
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));
            // 执行操作
            byte[] result = cipher.doFinal(Base64.decodeBase64(content));
            clearText = new String(result, charset);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                 | IllegalBlockSizeException | BadPaddingException | UnsupportedEncodingException e) {
            return content;
        }

        return clearText;
    }

    private static SecretKeySpec getSecretKey(final String key) throws NoSuchAlgorithmException {
        // 返回生成指定算法密钥生成器的 KeyGenerator 对象
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);

        // AES 要求密钥长度为 128
        SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
        secureRandom.setSeed(key.getBytes());

        kg.init(128, secureRandom);

        // 生成一个密钥
        SecretKey secretKey = kg.generateKey();

        // 转换为AES专用密钥
        return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);

    }
}