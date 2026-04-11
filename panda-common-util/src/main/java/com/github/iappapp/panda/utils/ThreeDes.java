package com.github.iappapp.panda.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;


/**
 * 字符串 DESede(3DES) 加密
 */
public class ThreeDes {

    // 定义 加密算法,可用 DES,DESede,Blowfish
    private static final String ALGORITHM = "DESede";

    // 24字节的密钥
    private static final byte[] keyBytes = { 0x11, 0x22, 0x4F, 0x58, (byte) 0x88, 0x10, 0x40, 0x38, 0x28, 0x25, 0x79,
            0x51, (byte) 0xCB, (byte) 0xDD, 0x55, 0x66, 0x77, 0x29, 0x74, (byte) 0x98, 0x30, 0x40, 0x36, (byte) 0xE2 };

    private ThreeDes() {
        // do nothing
    }

    /**
     */
    public static byte[] encryptMode(byte[] keybyte, byte[] src) {
        byte[] b = null;
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, ALGORITHM);
            // 加密
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     *
     */
    public static byte[] decryptMode(byte[] keybyte, byte[] src) {
        byte[] b = null;
        try {
            // 生成密钥
            SecretKey deskey = new SecretKeySpec(keybyte, ALGORITHM);
            // 解密
            Cipher c1 = Cipher.getInstance(ALGORITHM);
            c1.init(Cipher.DECRYPT_MODE, deskey);
            return c1.doFinal(src);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return b;
    }

    // 转换成十六进制字符串
    public static String byte2hex(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (byte value : b) {
            stmp = Integer.toHexString(value & 0XFF);
            if (stmp.length() == 1) {
                hs.append("0").append(stmp);
            } else {
                hs.append(stmp);
            }
        }
        return hs.toString().toUpperCase();
    }

    public static byte[] hex2byte(byte[] b) {
        byte[] b2 = new byte[b.length / 2];
        for (int n = 0; n < b.length; n += 2) {
            String item = new String(b, n, 2);
            b2[n / 2] = (byte) Integer.parseInt(item, 16);
        }
        return b2;
    }

    public static String encrypt(String messageid, String account, String udId) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());
        String szSrc = account + "&" + udId + "&" + messageid + "&" + date;
        String passkey = null;
        try {
            byte[] encoded = ThreeDes.encryptMode(keyBytes, szSrc.getBytes());
            passkey = ThreeDes.byte2hex(encoded);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passkey;
    }

    /**
     * 字符串转换成十六进制
     */
    public static String str2Hex(String s) {
        StringBuilder str = new StringBuilder();
        int length = s.length();
        for (int i = 0; i < length; i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str.append(s4);
        }
        return str.toString();
    }

}
