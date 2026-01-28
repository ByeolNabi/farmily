package com.ssafy.farmily.domain.member.controller;

import com.ssafy.farmily.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/auth/email-req")
    public Map<String, String> sendAuthCode(@RequestBody Map<String, String> request) {
        memberService.sendCode(request.get("email"));
        return Map.of("message", "발송 완료");
    }

    @PostMapping("/auth/email-verify")
    public Map<String, Boolean> verifyAuthCode(@RequestBody Map<String, String> request) {
        boolean isVerified = memberService.verifyCode(request.get("email"), request.get("code"));
        return Map.of("isVerified", isVerified);
    }

    @PostMapping("/users/signup")
    public Map<String, Long> signup(@RequestBody SignupRequest request) {
        Long userId = memberService.signup(request.email(), request.password(), request.name());
        return Map.of("userId", userId);
    }

    @PostMapping("/users/login")
    public Map<String, String> login(@RequestBody LoginRequest request) {
        return memberService.login(request.email(), request.password());
    }

    @PostMapping("/auth/refresh")
    public Map<String, String> reissue(@RequestBody ReissueRequest request) {
        String token = memberService.reissue(request.email(), request.refreshToken());
        return Map.of("accessToken", token);
    }

    @PatchMapping("/users/withdraw")
    public Map<String, String> withdraw(@RequestBody Map<String, String> request) {
        memberService.withdraw(request.get("email"));
        return Map.of("message", "탈퇴 완료");
    }

    @PatchMapping("/users/password")
    public Map<String, String> resetPassword(@RequestBody PasswordResetRequest request) {
        memberService.resetPassword(request.email(), request.newPassword());
        return Map.of("message", "변경 완료");
    }

    public record SignupRequest(String email, String password, String name) {}
    public record LoginRequest(String email, String password) {}
    public record PasswordResetRequest(String email, String newPassword) {}
    public record ReissueRequest(String email, String refreshToken) {}
}