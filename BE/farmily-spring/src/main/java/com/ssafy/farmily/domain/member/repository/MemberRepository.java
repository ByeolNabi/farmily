package com.ssafy.farmily.domain.member.repository;

import com.ssafy.farmily.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// JpaRepository<다룰대상, PK타입>
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 1. 이메일로 회원 찾기 (이게 없어서 에러가 난 겁니다!)
    Optional<Member> findByEmail(String email);

    // 2. 이메일 중복 검사 (이것도 꼭 있어야 해요!)
    boolean existsByEmail(String email);
}