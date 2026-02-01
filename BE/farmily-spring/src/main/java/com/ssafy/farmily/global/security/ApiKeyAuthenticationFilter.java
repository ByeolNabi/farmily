package com.ssafy.farmily.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

/**
 * 기기(LCD/Jetson) 인증을 위한 API Key 필터
 * 헤더에 X-Device-Key가 있고 내부 설정값과 일치하면 인증 허용
 */
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    @Value("${farmily.device.api-key}")
    private String deviceApiKey;

    private static final String API_KEY_HEADER = "X-Device-Key";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestApiKey = request.getHeader(API_KEY_HEADER);

        if (requestApiKey != null && requestApiKey.equals(deviceApiKey)) {
            // API Key가 일치하면 "DEVICE"라는 이름의 가상 사용자로 인증 처리
            UsernamePasswordAuthenticationToken authentication = 
                new UsernamePasswordAuthenticationToken("DEVICE", null, new ArrayList<>());
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
