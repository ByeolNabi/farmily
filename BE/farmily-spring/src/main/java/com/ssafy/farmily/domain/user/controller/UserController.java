package com.ssafy.farmily.domain.user.controller;

import com.ssafy.farmily.domain.user.service.UserService;
import com.ssafy.farmily.global.util.AuthUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthUtil authUtil;

    @PostMapping("/auth/email-req")
    public Map<String, String> sendAuthCode(@RequestBody Map<String, String> request) {
        userService.sendCode(request.get("email"));
        return Map.of("message", "발송 완료");
    }

    @PostMapping("/auth/email-verify")
    public Map<String, Boolean> verifyAuthCode(@RequestBody Map<String, String> request) {
        boolean isVerified = userService.verifyCode(request.get("email"), request.get("code"));
        return Map.of("isVerified", isVerified);
    }

    @PostMapping("/auth/signup")
    public Map<String, Long> signup(@RequestBody SignupRequest request) {
        Long userId = userService.signup(request.email(), request.password(), request.name());
        return Map.of("userId", userId);
    }

    @PostMapping("/auth/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        return userService.login(request.email(), request.password());
    }

    @PostMapping("/auth/refresh")
    public Map<String, String> reissue(@RequestBody ReissueRequest request) {
        String token = userService.reissue(request.email(), request.refreshToken());
        return Map.of("accessToken", token);
    }

    @PostMapping("/auth/logout")
    public Map<String, String> logout(@RequestHeader("Authorization") String token) {
        String accessToken = token.substring(7);
        String email = authUtil.getCurrentEmail();
        userService.logout(email, accessToken);
        return Map.of("message", "로그아웃 완료");
    }

    @PatchMapping("/users/withdraw")
    public Map<String, String> withdraw(@RequestBody Map<String, String> request) {
        userService.withdraw(request.get("email"));
        return Map.of("message", "탈퇴 완료");
    }

    // 비밀번호 변경 (로그인 필요)
    @PatchMapping("/users/password")
    public Map<String, String> changePassword(@RequestBody PasswordChangeRequest request) {
        // 현재 로그인한 사용자의 이메일을 가져옴
        String currentUserEmail = authUtil.getCurrentEmail();
        userService.resetPassword(currentUserEmail, request.newPassword());
        return Map.of("message", "변경 완료");
    }

    // 비밀번호 찾기 (로그인 불필요, 이메일 인증 필요)
    @PostMapping("/auth/password-reset")
    public Map<String, String> resetPasswordWithEmail(@RequestBody PasswordResetRequest request) {
        // 1. 이메일 인증 코드 확인
        boolean isVerified = userService.verifyCode(request.email(), request.code());
        if (!isVerified) {
            throw new IllegalArgumentException("인증 코드가 올바르지 않습니다.");
        }
        
        // 2. 비밀번호 재설정
        userService.resetPassword(request.email(), request.newPassword());
        return Map.of("message", "비밀번호가 재설정되었습니다.");
    }

    /**
     * FCM 토큰 업데이트
     */
    @PutMapping("/api/user/fcm-token")
    public Map<String, String> updateFcmToken(@RequestBody FcmTokenRequest request) {
        String email = authUtil.getCurrentEmail();
        userService.updateFcmToken(email, request.fcmToken());
        return Map.of("message", "FCM 토큰이 업데이트되었습니다.");
    }

    public record SignupRequest(String email, String password, String name) {}
    public record LoginRequest(String email, String password) {}
    public record PasswordChangeRequest(String newPassword) {}  // 로그인한 사용자의 비밀번호 변경
    public record PasswordResetRequest(String email, String code, String newPassword) {}  // 비밀번호 찾기 (이메일 인증)
    public record ReissueRequest(String email, String refreshToken) {}
    public record FcmTokenRequest(String fcmToken) {}
}
