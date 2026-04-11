package com.github.iappapp.panda.utils;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;

/**
 * @author liush2
 * @date 2019/8/27 18:00
 * @remarks
 */
public class JsonHelper {
    /**
     * 判断字符串是否为json格式
     *
     * @param str
     * @return
     */
    public static boolean isJsonObject(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }

        try {
            JSONObject jsonObject = JSONObject.parseObject(str);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static boolean isJsonArray(String str) {
        if (StringUtils.isEmpty(str)) {
            return false;
        }
        if (str.startsWith("{")){
            return false;
        }

        try {
            JSONObject.parseArray(str);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
