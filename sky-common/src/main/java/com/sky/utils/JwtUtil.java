package com.sky.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;


public class JwtUtil {

    // 生成token / JWT令牌
    public static String createJwt(String secretKey, Long ttlMillis, Map<String, Object> claims){
        //指定签名算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 创建截止时间
        Long expMillis = System.currentTimeMillis() + ttlMillis;
        Date exp = new Date(expMillis);

        //生成token
        JwtBuilder builder = Jwts.builder()
                //设置claims
                .setClaims(claims)
                //设置过期时间
                .setExpiration(exp)
                //设置签名
                .signWith(signatureAlgorithm, secretKey.getBytes(StandardCharsets.UTF_8));
        return builder.compact();
    }

    public static Claims parseJWT(String adminSecretKey, String token) {
        return Jwts.parser()
                .setSigningKey(adminSecretKey.getBytes(StandardCharsets.UTF_8))
                .parseClaimsJws(token)
                .getBody();
    }
}
