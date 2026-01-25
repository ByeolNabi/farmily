package com.ssafy.farmily.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // 1. 암호화 도구 (비밀번호를 안전하게 바꾸는 기계)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. 보안 필터 (문지기 설정)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 끄기 (Rest API에서는 보통 끕니다)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 세션 안 씀 (토큰 쓸 거니까)
                .authorizeHttpRequests(auth -> auth
                        // 👇 이 주소들은 아무나 들어올 수 있게 허락!
                        .requestMatchers("/users/signup", "/users/login", "/auth/**").permitAll()
                        // 나머지는 다 인증 필요함!
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}