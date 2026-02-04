package com.ssafy.farmily.domain.user.service;

import com.ssafy.farmily.domain.user.entity.User;
import com.ssafy.farmily.domain.user.repository.UserRepository;
import com.ssafy.farmily.global.util.JwtUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    // 1. 회원가입
    @Transactional
    public Long signup(String email, String password, String name) {
        // 기존 회원 조회
        userRepository.findByEmail(email).ifPresent(user -> {
            // 활동 중인 회원이면 에러
            if (!user.isDeleted()) {
                throw new IllegalStateException("이미 존재하는 회원입니다.");
            }
            // 탈퇴한 회원이면 복구 (재가입)
            user.recover(passwordEncoder.encode(password), name);
        });

        // 회원이 없으면 신규 가입
        if (!userRepository.existsByEmail(email)) {
             String encodedPassword = passwordEncoder.encode(password);
             User user = User.builder()
                     .email(email)
                     .password(encodedPassword)
                     .name(name)
                     .build();
             userRepository.save(user);
             return user.getId();
        }
        
        return userRepository.findByEmail(email).get().getId();
    }

    // 2. 로그인
    public Map<String, String> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));
        
        // 탈퇴한 회원 체크
        if (user.isDeleted()) {
            throw new IllegalArgumentException("회원 정보가 없습니다.");
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }
        String accessToken = jwtUtil.createToken(email, user.getId());
        String refreshToken = jwtUtil.createRefreshToken(email, user.getId());
        redisTemplate.opsForValue().set("RT:" + email, refreshToken, Duration.ofDays(14));
        return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
    }

    // 3. 로그아웃
    public void logout(String email, String accessToken) {
        redisTemplate.opsForValue().set("BL:" + accessToken, "logout", Duration.ofMinutes(30)); 
        redisTemplate.delete("RT:" + email);
    }

    // 3. 비밀번호 재설정
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email).orElseThrow();
        user.updatePassword(passwordEncoder.encode(newPassword));
    }

    // 4. 회원 탈퇴
    @Transactional
    public void withdraw(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.withdraw();
        redisTemplate.delete("RT:" + email);
    }

    // 5. 이메일 발송
    public void sendCode(String email) {
        // 이미 가입된 활동 중인 회원인지 확인
        if (userRepository.existsByEmail(email)) {
            User user = userRepository.findByEmail(email).get();
            if (!user.isDeleted()) {
                throw new IllegalArgumentException("이미 가입된 이메일입니다.");
            }
        }

        String code = String.valueOf(new java.util.Random().nextInt(900000) + 100000);
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email);
            helper.setSubject("[Farmily] 회원가입 인증코드입니다.");

            String htmlContent = """
                    <div style="background-color: #f8f9fa; padding: 20px; text-align: center;">
                        <h1 style="color: #2c3e50;">🌿 Farmily</h1>
                        <p>인증코드: <b style="font-size: 24px;">%s</b></p>
                    </div>
                    """.formatted(code);

            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) { throw new RuntimeException(e); }
        redisTemplate.opsForValue().set("AuthCode:" + email, code, Duration.ofMinutes(3));
    }

    public boolean verifyCode(String email, String code) {
        String saved = redisTemplate.opsForValue().get("AuthCode:" + email);
        return saved != null && saved.equals(code);
    }

    public String reissue(String email, String refreshToken) {
        String saved = redisTemplate.opsForValue().get("RT:" + email);
        if (saved == null || !saved.equals(refreshToken)) throw new IllegalArgumentException("무효한 토큰");
        User user = userRepository.findByEmail(email).orElseThrow();
        return jwtUtil.createToken(email, user.getId());
    }

    /**
     * FCM 토큰 업데이트
     */
    @Transactional
    public void updateFcmToken(String email, String fcmToken) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        user.updateFcmToken(fcmToken);
    }
}
