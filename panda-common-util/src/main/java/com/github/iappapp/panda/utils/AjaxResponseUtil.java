package com.github.iappapp.panda.utils;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSON;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

/**
 * @author quyf
 */
public class AjaxResponseUtil {

    /**
     * 一般用于返回jsonp的json消息
     */
    public static void jsonCallback(HttpServletResponse response, String jsonCallback, Map<String, Object> responseData) {
        String msg = JSON.toJSONString(responseData);
        msg = jsonCallback + "(" + msg + ")";
        returnData(response, msg);
    }

    /**
     * 返回Map格式的json消息
     */
    public static void returnData(HttpServletResponse response, Map<String, Object> returnData) {
        String jsonStr = JSON.toJSONString(returnData);
        returnData(response, jsonStr);
    }

    public static void returnData(HttpServletResponse response, String msg) {
        // 写入response
        try {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().write(msg);
            response.getWriter().flush();
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void returnData(HttpServletResponse response,int code,String err){
        String msg = "{\"code\":"+code+",\"err\":\""+err+"\"}";
        returnData(response,msg);
    }

    /**
     * 根据 format参数，返回不同格式的消息
     *
     * @param response
     * @param format:json,xml
     * @param responseData
     */
    public static void returnData(HttpServletResponse response, String format, Map<String, Object> responseData) {
        String msg = null;
        if (format.equals("json")) {
            msg = createJson(responseData);
        } else if (format.equals("xml")) {
            msg = createXml(responseData);
        }

        // 写入response
        returnData(response, msg);
    }

    /**
     * 根据 format参数，返回不同格式的消息
     *
     * @param response
     * @param response
     * @param Obj
     */
    public static void returnData(HttpServletResponse response, Object Obj) {
        String msg = JSON.toJSONString(Obj);

        // 写入response
        returnData(response, msg);
    }


    private static String createXml(Map<String, Object> responseData) {
        StringBuilder sb = new StringBuilder();

        sb.append("<data>");
        for (String key : responseData.keySet()) {
            sb.append("<").append(key).append(">");
            sb.append(responseData.get(key));
            sb.append("</").append(key).append(">");
        }
        sb.append("</data>");

        Document doc = null;
        try {
            doc = DocumentHelper.parseText(sb.toString());
        } catch (DocumentException e) {
            return null;
        }
        return doc.asXML();
    }


    public static String createJson(Map<String, Object> responseData) {
        return JSON.toJSONString(responseData);
    }

}
