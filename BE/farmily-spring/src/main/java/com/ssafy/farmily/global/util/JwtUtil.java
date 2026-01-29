package com.ssafy.farmily.global.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 비밀키 (32글자 이상 아무거나)
    private static final String SECRET_KEY = "farmily_secret_key_farmily_secret_key_1234";
    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 1. Access Token 생성 (1시간)
    public String createToken(String email) {
        long validTime = 1000L * 60 * 60; // 1시간
        return createToken(email, validTime);
    }

    // 2. Refresh Token 생성 (2주)
    public String createRefreshToken(String email) {
        long validTime = 1000L * 60 * 60 * 24 * 14; // 2주 (14일)
        return createToken(email, validTime);
    }

    // 내부적으로 쓰는 토큰 생성기
    private String createToken(String email, long validTime) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + validTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 3. 토큰에서 이메일 추출
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}