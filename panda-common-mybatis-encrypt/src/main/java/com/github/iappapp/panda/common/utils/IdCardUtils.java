package com.github.iappapp.panda.common.utils;

import java.util.regex.Pattern;

public class IdCardUtils {

    // 15位身份证
    private static final Pattern ID_15 = Pattern.compile("^[0-9]{15}$");
    // 18位身份证
    private static final Pattern ID_18 = Pattern.compile("^[0-9]{17}[0-9Xx]$");

    /**
     * 判断是否是明文身份证号（未加密）
     */
    public static boolean isPlainIdCard(String idCard) {
        if (idCard == null) {
            return false;
        }
        return ID_15.matcher(idCard).matches() || ID_18.matcher(idCard).matches();
    }

    /**
     * 判断是否可能是AES加密后的字符串（Base64）
     */
    public static boolean isEncrypted(String text) {
        if (text == null) {
            return false;
        }
        // AES Base64 编码后通常 > 20，且包含字母/数字/+/=，但不符合身份证规则
        if (isPlainIdCard(text)) {
            return false;
        }
        return text.matches("^[0-9A-Za-z+/=]+$") && text.length() > 20;
    }

}
