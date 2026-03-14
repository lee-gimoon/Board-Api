package com.board.api.domain.member.repository;
// 이제 데이터베이스라는 거대한 창고에서 데이터를 넣고 빼는 **'창고 관리인'**인 **리포지토리(Repository)**를 만들 차례입니다.
// 스프링 데이터 JPA(Spring Data JPA)의 가장 놀라운 점은, 우리가 클래스가 아닌 인터페이스만 정의해도 실제 데이터베이스와 통신하는 코드를 스프링이 알아서 다 채워준다는 것입니다.

import com.board.api.domain.member.entity.Member;

// 스프링 데이터 JPA가 제공하는 **'최상위 인터페이스 설계도'**를 내 코드로 import함.
// JpaRepository는 단독으로 존재하는 게 아니라, 여러 인터페이스를 상속받아 만들어진 종합 선물 세트 같은 인터페이스입니다.
// Repository, CrudRepository, PagingAndSortingRepository, JpaRepository
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * [핵심 포인트]
 * 1. interface로 선언합니다.
 * 2. JpaRepository<Member, Long>를 상속받습니다.
 * - Member: 이 리포지토리가 다룰 엔티티 클래스
 * - Long: 그 엔티티의 @Id(PK) 타입
 */

// Spring Data JPA -> JPA 인터페이스 -> Hibernate 구현체 순으로 호출되어 실행되는 구조입니다.
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 이메일로 회원을 찾는 기능 추가
    // 이 메서드 이름을 보고 스프링이 "SELECT * FROM members WHERE email = ?" 쿼리를 자동으로 생성합니다.
    Optional<Member> findByEmail(String email);

    // 닉네임 중복 체크를 위해 존재 여부 확인
    boolean existsByEmail(String email); // 이름이 곧 로직이다: 쿼리 메서드 (Query Derivation)
    // Spring Data JPA는 메서드 이름에서 특정 키워드를 추출하여 쿼리를 생성합니다.
    // exists...By	존재 여부 확인 (boolean 반환)
}

// Spring Data JPA의 마법:
// 인터페이스만 선언하면 스프링이 런타임에 이 인터페이스를 구현한
// '동적 프록시(대리인) 객체'를 자동으로 생성하여 주입(DI)해줍니다.
// 프록시의 목적:	기능(로직) 자체를 자동 생성
// 프록시 생성 여부: 무조건 생성
// 결론: "스프링은 인터페이스만으로 객체를 만들 수 있는 능력이 있지만, 그 인터페이스가 어떤 표준(JPA 등)을 따르고 있거나,
// 혹은 **이미 만든 클래스를 감싸서 도와줄 이유(AOP)**가 있을 때만 마법을 부린다!.
