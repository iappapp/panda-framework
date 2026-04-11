/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.bouncycastle.jce.provider.BouncyCastleProvider
 *  org.jasypt.encryption.pbe.StandardPBEStringEncryptor
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.github.iappapp.panda.common.nacos.utils;

import java.lang.reflect.Field;
import java.security.KeyFactory;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Ecc256EncryUtil {
    private static final Logger log = LoggerFactory.getLogger(Ecc256EncryUtil.class);
    private static final String JASYPT_ENCRYPTOR_PASSWORD = "jasypt.encryptor.password";
    public static final String ECC_START = "ECC(";
    private static final String ENC_START = "ENC(";
    private static final String BC = "BC";
    private static final String EC = "EC";
    private static final String ALGORITHM = "PBEWITHSHA256AND128BITAES-CBC-BC";
    private static final String ECIES = "ECIES";

    static {
        Security.addProvider(new BouncyCastleProvider());
    }


    public static String getDecryption(String ciphertext, String appJasyptEncryptorPassword) {
        if (ciphertext == null) {
            return null;
        }
        if (null == appJasyptEncryptorPassword) {
            log.info("jasypt encryptor password is null, maybe you need to check jasypt.encryptor.password in application.yml");
            return ciphertext;
        }
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setProviderName(BC);
        encryptor.setAlgorithm(ALGORITHM);
        String softValue = System.getProperty(JASYPT_ENCRYPTOR_PASSWORD);
        if (softValue == null && !"-1".equals(appJasyptEncryptorPassword)) {
            softValue = appJasyptEncryptorPassword;
        }
        String plaintext = ciphertext;
        if (ciphertext.startsWith(ENC_START) && softValue != null) {
            encryptor.setPassword(softValue);
            ciphertext = ciphertext.substring(4, ciphertext.length() - 1);
            plaintext = encryptor.decrypt(ciphertext);
        }
        return plaintext;
    }

    public static String getEncryption(String plaintext, String pulibcKey) throws Exception {
        if (StringUtils.isBlank(plaintext)) {
            return plaintext;
        }
        ECPublicKey publicKey = Ecc256EncryUtil.string2PublicKey(pulibcKey);
        Cipher cipher = Cipher.getInstance(ECIES, BC);
        Ecc256EncryUtil.setFieldValueByFieldName(cipher);
        cipher.init(1, publicKey);
        byte[] bytes = cipher.doFinal(plaintext.getBytes());
        return ECC_START + Base64.getEncoder().encodeToString(bytes);
    }

    private static ECPublicKey string2PublicKey(String pubStr) throws Exception {
        if (StringUtils.isBlank(pubStr)) {
            return null;
        }
        byte[] keyBytes = Base64.getDecoder().decode(pubStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(EC, BC);
        ECPublicKey publicKey = (ECPublicKey)keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    public static void setFieldValueByFieldName(Cipher object) {
        if (object == null) {
            return;
        }
        Class<?> cipher = object.getClass();
        String cryptoPermStr = "cryptoPerm";
        String maxKeySizeStr = "maxKeySize";
        try {
            Field cipherField = cipher.getDeclaredField(cryptoPermStr);
            cipherField.setAccessible(true);
            Object cryptoPerm = cipherField.get(object);
            Class<?> c = cryptoPerm.getClass();
            Field cryptoPermField = c.getDeclaredField(maxKeySizeStr);
            cryptoPermField.setAccessible(true);
            cryptoPermField.set(cryptoPerm, 257);
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("setFieldValueByFieldName is error", e);
        }
    }
}

