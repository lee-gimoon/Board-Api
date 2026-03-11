package com.board.api.domain.member.repository;
// 이제 데이터베이스라는 거대한 창고에서 데이터를 넣고 빼는 **'창고 관리인'**인 **리포지토리(Repository)**를 만들 차례입니다.
//스프링 데이터 JPA(Spring Data JPA)의 가장 놀라운 점은, 우리가 클래스가 아닌 인터페이스만 정의해도 실제 데이터베이스와 통신하는 코드를 스프링이 알아서 다 채워준다는 것입니다.

import com.board.api.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * [핵심 포인트]
 * 1. interface로 선언합니다.
 * 2. JpaRepository<Member, Long>를 상속받습니다.
 * - Member: 이 리포지토리가 다룰 엔티티 클래스
 * - Long: 그 엔티티의 @Id(PK) 타입
 */

public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일로 회원을 찾는 기능 추가
    // 이 메서드 이름을 보고 스프링이 "SELECT * FROM members WHERE email = ?" 쿼리를 자동으로 생성합니다.
    Optional<Member> findByEmail(String email);

    // 닉네임 중복 체크를 위해 존재 여부 확인
    boolean existsByEmail(String email); // 이름이 곧 로직이다: 쿼리 메서드 (Query Derivation)
    // Spring Data JPA는 메서드 이름에서 특정 키워드를 추출하여 쿼리를 생성합니다.
    // exists...By	존재 여부 확인 (boolean 반환)
}

// 스프링의 마법 (Proxy: 대리인): 애플리케이션이 실행될 때, 스프링이 이 인터페이스를 보고 **실제 구현체(클래스)**를 메모리상에 몰래 만듭니다.
