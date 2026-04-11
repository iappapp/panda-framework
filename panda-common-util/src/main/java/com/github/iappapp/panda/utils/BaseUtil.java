package com.github.iappapp.panda.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author liush2
 * @date 2019/11/22 16:52
 * @remarks
 */
public class BaseUtil {

    public static String generatorOrderNo() {
        Long timeStamp = System.currentTimeMillis();
        Random random = new Random();
        int suffix = random.nextInt(100);
        String orderNo = timeStamp + String.format("%02d", suffix);
        return orderNo;
    }

    public static String getShortMobile(String mobile) {
        if (StringUtils.isEmpty(mobile)) {
            return null;
        } else {
            return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
        }
    }


    public static boolean isMobilePhone(String s) {
        return StringUtils.isEmpty(s) ? false : Pattern.matches("^(13|14|15|16|17|18|19)\\d{9}$", s);
    }

}
