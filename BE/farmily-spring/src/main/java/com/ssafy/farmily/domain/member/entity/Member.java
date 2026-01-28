package com.ssafy.farmily.domain.member.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users") // 👈 친구가 만든 테이블 이름이 'users'입니다!
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // SQL 컬럼명이 'id'이므로 변수명도 id로 맞춤 (자동 매핑)

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 255)
    private String password; // 소셜로그인 고려하면 nullable일 수도 있어서 체크 필요

    @Column(length = 100)
    private String name; // 👈 친구 SQL은 'nickname' 대신 'name'을 씀

    @Column(name = "profile_image_url") // 👈 DB는 스네이크 표기법, 자바는 카멜 표기법 매핑
    private String profileImageUrl;

    @Column(name = "fcm_token")
    private String fcmToken;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

//     @Column(name = "is_deleted")
//     private boolean isDeleted = false;

    @Builder
    public Member(String email, String password, String name, String profileImageUrl, String fcmToken) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.fcmToken = fcmToken;
    }
}