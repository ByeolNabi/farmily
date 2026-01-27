package com.ssafy.farmily.domain.member.controller;

import com.ssafy.farmily.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 1. 인증코드 발송 요청 (POST /auth/email-req)
    @PostMapping("/auth/email-req")
    public String sendAuthCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        memberService.sendCode(email);
        return "인증코드가 발송되었습니다. (유효시간 3분)";
    }

    // 2. 인증코드 검증 (POST /auth/email-verify)
    @PostMapping("/auth/email-verify")
    public Map<String, Boolean> verifyAuthCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        boolean isVerified = memberService.verifyCode(email, code);
        return Map.of("isVerified", isVerified); // JSON { "isVerified": true } 반환
    }

    // 3. 회원가입 (POST /users/signup)
    @PostMapping("/users/signup")
    public Map<String, Long> signup(@RequestBody SignupRequest request) {
        Long userId = memberService.signup(request.email, request.password);
        return Map.of("userId", userId);
    }

    // 4. 로그인 (POST /users/login)
    @PostMapping("/users/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        // 서비스가 이제 Map을 리턴해줍니다 (accessToken, refreshToken)
        return memberService.login(request.email(), request.password());
    }

    // 5. 토큰 재발급 요청 (POST /auth/reissue)
    @PostMapping("/auth/refresh")
    public Map<String, String> reissue(@RequestBody ReissueRequest request) {
        String newAccessToken = memberService.reissue(request.email(), request.refreshToken());
        return Map.of("accessToken", newAccessToken);
    }

    // 6. 회원 탈퇴 (PATCH /users/withdraw) - 내 정보 수정이라 보통 PATCH나 DELETE 씀
    @PatchMapping("/users/withdraw")
    public String withdraw(@RequestBody Map<String, String> request) {
        memberService.withdraw(request.get("email"));
        return "회원 탈퇴 처리되었습니다.";
    }

    // 7. 비밀번호 재설정 (PATCH /users/password)
    @PatchMapping("/users/password")
    public String resetPassword(@RequestBody PasswordResetRequest request) {
        memberService.resetPassword(request.email(), request.newPassword());
        return "비밀번호가 재설정되었습니다.";
    }

    // DTO
    public record PasswordResetRequest(String email, String newPassword) {}
    public record ReissueRequest(String email, String refreshToken) {}
    public record SignupRequest(String email, String password) {}
    public record LoginRequest(String email, String password) {}
}