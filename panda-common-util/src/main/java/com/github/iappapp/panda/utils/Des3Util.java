package com.github.iappapp.panda.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;

/**标准 des 加解密工具类*/
public class Des3Util {
    private static final String ALGORITHM = "DESede";
    // 转换成十六进制字符串
    public static String byte2hex(byte[] b) {
        String hs = "";
        String stmp = "";
        for (int n = 0; n < b.length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1)
                hs = hs + "0" + stmp;else
                hs = hs + stmp;
        }
        return hs.toUpperCase();
    }
    public static byte[] hexToByte(String s) throws IOException {
        int i = s.length() / 2;
        byte abyte0[] = new byte[i];
        int j = 0;
        if (s.length() % 2 != 0)
            throw new IOException(
                    "hexadecimal string with odd number of characters");
        for (int k = 0; k < i; k++) {
            char c = s.charAt(j++);
            int l = "0123456789abcdef0123456789ABCDEF".indexOf(c);
            if (l == -1)
                throw new IOException(
                        "hexadecimal string contains non hex character");
            int i1 = (l & 0xf) << 4;
            c = s.charAt(j++);
            l = "0123456789abcdef0123456789ABCDEF".indexOf(c);
            i1 += l & 0xf;
            abyte0[k] = (byte) i1;
        }return abyte0;
    }
    /**des 加密，注意，其中 key 的长度为 32 位*/
    public static String encode(String para, String key) throws Exception {
        byte[] text = para.getBytes("UTF-8"); // 待加/解密的数据
// 密钥数据
        byte[] keyData = build3DesKey(key);
        String fullAlg = ALGORITHM + "/CBC/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(fullAlg);
        int blockSize = cipher.getBlockSize();
        byte[] iv = new byte[blockSize];
        for (int i = 0; i < blockSize; ++i) {
            iv[i] = 0;
        }
        SecretKey secretKey = new SecretKeySpec(keyData, ALGORITHM);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] cipherBytes = cipher.doFinal(text);
        return byte2hex(cipherBytes);
    }
    /**构造 3DES 加解密方法 key*/
    private static byte[] build3DesKey(String keyStr) throws Exception {
        byte[] key = new byte[24];byte[] temp = keyStr.getBytes("UTF-8");
        if (key.length > temp.length) {
            System.arraycopy(temp, 0, key, 0, temp.length);
        } else {
            System.arraycopy(temp, 0, key, 0, key.length);
        }
        return key;
    }
    /**对字符串进行解密*/
    public static String decode(String para, String key) throws Exception {
        byte[] text = hexToByte(para); // 待加/解密的数据
// 密钥数据
// byte[] keyData = Base64.encode(key.getBytes("UTF-8"), Base64.DEFAULT);
        byte[] keyData = build3DesKey(key);
        String fullAlg = ALGORITHM + "/CBC/PKCS5Padding";
        Cipher cipher = Cipher.getInstance(fullAlg);
        int blockSize = cipher.getBlockSize();
        byte[] iv = new byte[blockSize];
        for (int i = 0; i < blockSize; ++i) {
            iv[i] = 0;
        }
        SecretKey secretKey = new SecretKeySpec(keyData, ALGORITHM);IvParameterSpec ivSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        return new String(cipher.doFinal(text));
    }



    public static String des3EncodeCBC(String key, String keyiv, String data) throws Exception {
        byte[] str5 = des3EncodeCBC(key.getBytes(), keyiv.getBytes(), data.getBytes("utf-8"));
        return Base64.getEncoder().encodeToString(str5);
    }

    /**
     * CBC加密
     *
     * @param key   密钥
     * @param keyiv IV
     * @param data  明文
     * @return Base64编码的密文
     * @throws Exception
     */
    public static byte[] des3EncodeCBC(byte[] key, byte[] keyiv, byte[] data) throws Exception {
        Key deskey = null;
        DESedeKeySpec spec = new DESedeKeySpec(key);
        SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("desede");
        deskey = keyfactory.generateSecret(spec);
        Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        IvParameterSpec ips = new IvParameterSpec(keyiv);
        cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
        byte[] bOut = cipher.doFinal(data);
        return bOut;
    }


    /**
     * CBC 解密
     *
     * @param key       密钥
     * @param keyiv     IV
     * @param data      Base64编码的密文
     * @return          加密的明文
     * @throws Exception
     */
    public static String des3DecodeCBC(String key, String keyiv, String data) throws Exception {
        final byte[] bytesrc = Base64.getDecoder().decode(data);

        // --解密的key
        final DESedeKeySpec desKeySpec = new DESedeKeySpec(key.getBytes());
        final SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("desede");
        final Key secretKey = keyFactory.generateSecret(desKeySpec);

        // --向量
        final IvParameterSpec iv = new IvParameterSpec(keyiv.getBytes());

        final Cipher cipher = Cipher.getInstance("desede" + "/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        final byte[] retByte = cipher.doFinal(bytesrc);

        return new String(retByte);
    }
}
