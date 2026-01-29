package com.ssafy.farmily.global.util;

import com.ssafy.farmily.domain.user.entity.User;
import com.ssafy.farmily.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthUtil {

    private final UserRepository userRepository;

    /**
     * 현재 인증된 사용자의 User 엔티티 반환
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        
        String email = (String) authentication.getPrincipal();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
    }

    /**
     * 현재 인증된 사용자의 ID 반환 (JWT에서 직접 추출)
     */
    public Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 사용자입니다.");
        }
        
        // JWT 토큰에서 userId 직접 추출 (DB 조회 불필요)
        String email = (String) authentication.getPrincipal();
        // Note: SecurityContext에는 이메일만 저장되므로, 필요시 JwtAuthenticationFilter에서 userId도 저장하도록 수정 필요
        return getCurrentUser().getId();
    }

    /**
     * 현재 인증된 사용자의 이메일 반환
     */
    public String getCurrentEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (String) authentication.getPrincipal();
    }
}
