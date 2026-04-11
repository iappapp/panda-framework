package com.github.iappapp.panda.common.auth.util;

import com.alibaba.fastjson.JSON;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.iappapp.panda.common.auth.model.UserInfo;

import java.util.Calendar;
import java.util.Date;

public class JwtUtils {
    private final static String SECRET = "12345678@0";

    private static String encodeToken(String data, String secret) {
        Calendar instance = Calendar.getInstance();
        instance.add(Calendar.HOUR, 1);
        String token = JWT.create()
                .withSubject(data)
                .withExpiresAt(instance.getTime())
                .withIssuedAt(new Date())
                .sign(Algorithm.HMAC256(secret));

        return token;
    }

    public static String decodeToken(String data, String secret) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        DecodedJWT decodedJWT = verifier.verify(data);
        return decodedJWT.getSubject();
    }

    public static void main(String[] args) {
        UserInfo userInfo = new UserInfo();
        userInfo.setIs5g(0);
        userInfo.setMobile("13212341234");
        userInfo.setProvince("34");
        userInfo.setIsTelecom(1);
        userInfo.setLoginTime(new Date().getTime());
        System.out.println(encodeToken(JSON.toJSONString(userInfo), SECRET));
        System.out.println(decodeToken(encodeToken(JSON.toJSONString(userInfo), SECRET), SECRET));
    }
}
