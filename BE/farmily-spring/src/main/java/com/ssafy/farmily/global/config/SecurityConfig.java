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
            // 1. CSRF 비활성화 (REST API 서버는 상태를 저장하지 않으므로 보통 끕니다)
            .csrf(csrf -> csrf.disable()) 
            
            // 2. JWT 인증을 사용하므로 세션은 만들지 않음
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 3. 경로별 권한 설정 (이 부분이 핵심!)
            .authorizeHttpRequests(auth -> auth
                // 👇 회원가입, 로그인, 이메일 인증은 "로그인 없이" 가능해야 함
                .requestMatchers("/users/signup", "/users/login").permitAll()
                .requestMatchers("/auth/**").permitAll() 
                
                // 👇 그 외 모든 요청은 반드시 "인증(JWT 토큰)"이 있어야 함
                .anyRequest().authenticated()
            );

        return http.build();
    }
}