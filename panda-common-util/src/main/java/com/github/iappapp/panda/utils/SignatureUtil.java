package com.github.iappapp.panda.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


/**
 * @author xiongth
 */
public class SignatureUtil {
    /**
     * HMAC-SHA1算法名称
     */
    private final static String ALGORITHM_HMACSHA1 = "HmacSHA1";

    public static String hmacsha1(String data, String key) {
        byte[] byteHMAC = null;
        try {
            Mac mac = Mac.getInstance(ALGORITHM_HMACSHA1);
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(), ALGORITHM_HMACSHA1);
            mac.init(spec);
            byteHMAC = mac.doFinal(data.getBytes());
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return ByteFormat.toHex(byteHMAC);
    }

}
