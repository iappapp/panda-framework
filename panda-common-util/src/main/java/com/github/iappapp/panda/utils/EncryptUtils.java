/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cn.hutool.core.util.CharsetUtil
 *  cn.hutool.crypto.Mode
 *  cn.hutool.crypto.Padding
 *  cn.hutool.crypto.digest.HMac
 *  cn.hutool.crypto.digest.HmacAlgorithm
 *  cn.hutool.crypto.symmetric.AES
 *  cn.hutool.crypto.symmetric.SymmetricAlgorithm
 *  org.apache.commons.codec.binary.Base64
 *  org.apache.commons.codec.binary.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.github.iappapp.panda.utils;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.digest.HMac;
import cn.hutool.crypto.digest.HmacAlgorithm;
import cn.hutool.crypto.symmetric.AES;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EncryptUtils {
    private static final Logger log = LoggerFactory.getLogger(EncryptUtils.class);
    private static final byte[] IV = "0102030405060708".getBytes(Charset.forName("utf-8"));
    private static final String ENCRYPT_TYPE_RSA = "RSA";

    public static SecretKey generateKey(String seed) {
        return EncryptUtils.generateKey(128, seed);
    }

    public static SecretKey generateKey(int size, String seed) {
        try {
            KeyGenerator kg = KeyGenerator.getInstance(SymmetricAlgorithm.AES.getValue());
            SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
            sr.setSeed(seed.getBytes(CharsetUtil.CHARSET_UTF_8));
            kg.init(size, sr);
            return kg.generateKey();
        }
        catch (NoSuchAlgorithmException e) {
            log.error("generateKey failed", (Throwable)e);
            return null;
        }
    }

    public static byte[] aesEncrypt(byte[] content, String seed) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, EncryptUtils.generateKey(seed).getEncoded(), IV);
        return aes.encrypt(content);
    }

    public static byte[] aesDecrypt(byte[] content, String seed) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, EncryptUtils.generateKey(seed).getEncoded(), IV);
        return aes.decrypt(content);
    }

    public static byte[] aesEncrypt(byte[] content, String seed, byte[] iv) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, EncryptUtils.generateKey(seed).getEncoded(), iv);
        return aes.encrypt(content);
    }

    public static byte[] aesDecrypt(byte[] content, String seed, byte[] iv) {
        AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, EncryptUtils.generateKey(seed).getEncoded(), iv);
        return aes.decrypt(content);
    }

    public static String hmacDigest(byte[] data, String key) {
        HMac mac = new HMac(HmacAlgorithm.HmacSHA256, key.getBytes(Charset.forName("utf-8")));
        return org.apache.commons.codec.binary.Base64.encodeBase64String((byte[])mac.digest(data));
    }

    public static String rsaEncrypt(String context, String publicKey) {
        String rsaPassword = null;
        try {
            byte[] decoded = Base64.getDecoder().decode(publicKey.getBytes(StandardCharsets.UTF_8));
            RSAPublicKey pubKey = (RSAPublicKey)KeyFactory.getInstance(ENCRYPT_TYPE_RSA).generatePublic(new X509EncodedKeySpec(decoded));
            Cipher rsa = Cipher.getInstance(ENCRYPT_TYPE_RSA);
            rsa.init(1, pubKey);
            rsaPassword = StringUtils.newStringUsAscii((byte[])Base64.getEncoder().encode(rsa.doFinal(context.getBytes(StandardCharsets.UTF_8))));
        }
        catch (Exception e) {
            log.error("RSA \u52a0\u5bc6\u5931\u8d25 publicKey={}, context={}.", new Object[]{publicKey, context, e});
        }
        return rsaPassword;
    }
}

