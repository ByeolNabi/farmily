package com.ssafy.farmily.domain.member.service;
import com.ssafy.farmily.domain.member.entity.Member;
import com.ssafy.farmily.domain.member.entity.Role;
import com.ssafy.farmily.domain.member.repository.MemberRepository;
import com.ssafy.farmily.global.util.JwtUtil; // 👈 JwtUtil
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor; // 👈 롬복
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder; // 👈 비밀번호 암호화
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final JavaMailSender mailSender;
    private final StringRedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    private static final String AUTH_PREFIX = "AuthCode:";

    // 1. 인증코드 발송
    public void sendCode(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }

        String code = createCode();

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setSubject("[Farmily] 회원가입 인증코드입니다.");

            // 이메일 HTML 디자인
            String htmlContent = """
                <div style="background-color: #f8f9fa; padding: 20px; text-align: center; font-family: 'Malgun Gothic', sans-serif;">
                    <div style="background-color: white; padding: 40px; border-radius: 10px; border: 1px solid #e9ecef; display: inline-block;">
                        <h1 style="color: #2c3e50; margin-bottom: 30px;">🌿 Farmily</h1>
                        <p style="font-size: 16px; color: #555;">아래 인증코드를 입력하여 가입을 완료해주세요.</p>
                        <div style="background-color: #e8f5e9; padding: 15px 30px; margin: 20px 0; border-radius: 5px; display: inline-block;">
                            <span style="font-size: 24px; font-weight: bold; letter-spacing: 5px; color: #2e7d32;">%s</span>
                        </div>
                        <p style="font-size: 14px; color: #888;">(인증코드는 3분간 유효합니다.)</p>
                    </div>
                </div>
                """.formatted(code);

            helper.setText(htmlContent, true);
            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("메일 발송에 실패했습니다.", e);
        }

        redisTemplate.opsForValue().set(AUTH_PREFIX + email, code, Duration.ofMinutes(3));
    }

    // 2. 인증코드 검증
    public boolean verifyCode(String email, String code) {
        String savedCode = redisTemplate.opsForValue().get(AUTH_PREFIX + email);
        return savedCode != null && savedCode.equals(code);
    }

    // 3. 회원가입 (암호화 추가됨!)
    @Transactional
    public Long signup(String email, String password) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(password);

        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .role(Role.USER)
                .build();
        memberRepository.save(member);
        return member.getId();
    }

    // 4. 로그인 (새로 추가됨!)
    public String login(String email, String password) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
        }

        return jwtUtil.createToken(email);
    }

    private String createCode() {
        return String.valueOf(new Random().nextInt(900000) + 100000);
    }
}