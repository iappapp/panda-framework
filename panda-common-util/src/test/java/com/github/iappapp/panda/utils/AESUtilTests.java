package com.github.iappapp.panda.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author iappapp
 * @date 2020/9/22 14:36
 */
@Slf4j
public class AESUtilTests {

    @Test
    public void encrypt() {
        String result = AESUtil.encrypt("13212341234", "123456789");
        String decrypt = AESUtil.decrypt(result, "123456789");
        log.info("result={} decrypt={}", result, decrypt);
        Assert.assertTrue(decrypt.equals("13212341234"));
    }
}
