package com.ssafy.farmily.global.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 비밀키 (실무에서는 application.yml에 숨겨야 하지만, 지금은 연습이니까 여기에!)
    // 32글자 이상이어야 안전합니다. 아무거나 길게 치세요.
    private static final String SECRET_KEY = "farmily_secret_key_farmily_secret_key_1234";

    private final Key key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    // 토큰 생성 (로그인 성공하면 이거 줌)
    public String createToken(String email) {
        long validTime = 1000L * 60 * 60; // 1시간 유효

        return Jwts.builder()
                .setSubject(email) // 토큰 주인 (이메일)
                .setIssuedAt(new Date()) // 발행 시간
                .setExpiration(new Date(System.currentTimeMillis() + validTime)) // 만료 시간
                .signWith(key, SignatureAlgorithm.HS256) // 도장 찍기 (암호화)
                .compact();
    }
}