package com.github.iappapp.panda.utils;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @author xiongth
 */
public class RequestUtil {
    public static String returnData(int code, String errMsg) {
        Map<String, Object> map = new HashMap<>();
        map.put("errorDescription", errMsg);
        map.put("code", code);
        return JSON.toJSONString(map);
    }

    public static String getRequestBody(HttpServletRequest request) {
        String res = "";
        try (BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));) {
            StringBuilder sb = new StringBuilder();
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
            }
            res = sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return res;
    }
}
