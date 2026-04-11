package com.github.iappapp.panda.utils;

/**
 * @author xiongth
 */
public class ByteFormat {
    private final static char[] HEX = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * 将byte数组转换为十六进制文本
     */
    public static String toHex(byte[] buf) {
        if (buf == null || buf.length == 0) {
            return "";
        }
        StringBuilder out = new StringBuilder();
        for (byte b : buf) {
            out.append(HEX[(b >> 4) & 0x0f]).append(HEX[b & 0x0f]);
        }
        return out.toString();
    }
}
