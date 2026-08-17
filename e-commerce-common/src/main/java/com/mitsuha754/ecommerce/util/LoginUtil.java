package com.mitsuha754.ecommerce.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
@ConfigurationProperties(prefix = "jwt")
@Data
public class LoginUtil {

    // 密钥（自己随便改，越长越安全）
    private String secret;

    private int accessExpire;

    private int refreshExpire;

    private Key getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 生成 token
    public  String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis() + accessExpire))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 解析 token
    public  Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public  String generateRefreshToken(){
        UUID uuid = UUID.randomUUID();
        return uuid
                .toString()
                .replace("-","");
    }

}