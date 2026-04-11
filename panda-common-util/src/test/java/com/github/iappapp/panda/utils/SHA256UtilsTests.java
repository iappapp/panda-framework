package com.github.iappapp.panda.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author iappapp
 * @date 2020/9/24 10:35
 */
public class SHA256UtilsTests {
    @Test
    public void test() {
        String secret = "1234567890";
        String channelCode = "10020";
        String mobile = "18600261941";
        String currentTime = "20200924090700";
        String orderId = "1234567890";
        String sign = "";
        sign = SHA256Utils.SHA256(channelCode + mobile + orderId + currentTime + secret);
        System.out.println(sign);
        Assert.assertEquals("34a0478fadec603012fb68539a3e6c189c8a9e2989e81ece37113a5a14644e0d", sign);
    }
}
