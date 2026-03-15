package com.board.api.domain.member.entity;
// 실제 회원 정보를 담을 클래스를 만듭니다. BaseTimeEntity를 상속받습니다.

import com.board.api.global.common.BaseTimeEntity;
// JPA가 "표준 규격"이라고 하면 뭔가 거창한 문서처럼 느껴지지만, 개발자 입장에서 보는 실체는 jakarta.persistence 패키지 안에 들어있는 인터페이스와 어노테이션들의 모음입니다.
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [핵심 포인트]
 * 1. @Entity: 이 클래스가 DB의 테이블과 1:1로 매핑되는 객체임을 선언합니다. (**"JPA라는 규칙을 준수하여 작성된 데이터 객체"**입니다.)
 * 2. extends BaseTimeEntity: 위에서 만든 시간 자동 관리 기능을 물려받습니다.
 */
// 클래스 레벨 어노테이션 (@Entity, @Table, @Getter): "이 클래스는 JPA(EntityManager)가 관리하는 엔티티고, DB의 members 테이블과 연결되며, 모든 필드의 값을 읽을 수 있다"고 선언합니다.
@Entity // "자바의 클래스와 DB의 테이블을 1:1로 짝지어줄게!" 하는 기술이 ORM(Object-Relational Mapping)이며, 그 짝짓기의 명찰이 바로 @Entity입니다.
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 아무데서나 객체를 생성하지 못하게 막아 안전성을 높입니다. JPA가 내부적으로 객체를 생성할 때만 쓸 수 있도록 PROTECTED로 막아둔 훌륭한 방어 코드입니다.
@Table(name = "members") // DB에는 members라는 이름의 테이블로 생성됩니다.
public class Member extends BaseTimeEntity { // extends BaseTimeEntity: 이 한 줄 덕분에 Member 클래스는 createdAt, updatedAt 필드를 직접 쓰지 않아도 갖게 됩니다. (DB 테이블 members에도 이 두 컬럼이 자동으로 생깁니다.)

    @Id // 기본 키(Primary Key)임을 나타냅니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 번호를 DB가 자동으로 1, 2, 3... 채워줍니다.
    private Long id;

    @Column(nullable = false, unique = true) // 이메일은 필수고 중복될 수 없습니다.
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    // 객체를 생성할 때 new Member("a@a.com", "123", "nick")처럼 순서에 의존하지 않고, Member.builder().email("a@a.com")...build()처럼 명확하고 안전하게 만들 수 있게 해줍니다.
    @Builder // 빌더 패턴을 사용하여 객체 생성을 안전하고 가독성 좋게 만듭니다.
    public Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}//class

// 사용자님이 MemberService에서 memberRepository.save(member)를 호출하는 순간 일어나는 마법입니다.
// 1. JPA의 감지: "어? Member 객체가 새로 저장(save)되려 하네?"
// 2. 리스너 호출: Member는 BaseTimeEntity를 상속받았고, 거긴 @EntityListeners가 달려있습니다. 감시자(AuditingEntityListener)가 출동합니다.
// 3. 시간 주입: 감시자가 현재 컴퓨터 시계를 보고, @CreatedDate와 @LastModifiedDate가 붙은 필드에 2026-03-16 02:12:00 같은 시간을 쓱 채워 넣습니다. (이때 JpaAuditConfig가 켜져 있어야만 작동합니다.)
// 4. DB 쿼리 실행: 최종적으로 시간이 모두 채워진 완벽한 데이터가 DB에 INSERT 됩니다.

// JPA용 기본 생성자 (@NoArgsConstructor)
// "빈 상자가 먼저 필요해!"
// 주인님: JPA (Hibernate)

// 스프링 DI용 생성자 (@RequiredArgsConstructor)
// "부품을 다 끼워야 가동할 수 있어!"
// 주인님: 스프링 컨테이너 (Spring Framework)

// Entity 클래스: @NoArgsConstructor는 필수입니다. 하지만 @RequiredArgsConstructor는 보통 안 씁니다.
// 엔티티는 부품을 주입받는 게 아니라 데이터를 담는 통이기 때문입니다. 대신 우리는 데이터를 명확히 넣기 위해 **@Builder**를 주로 씁니다.

// Service 클래스: @RequiredArgsConstructor가 필수입니다. 하지만 @NoArgsConstructor는 절대 쓰면 안 됩니다.
// 부품(Repository)이 없는 서비스 객체는 아무 일도 못 하는 고장 난 기계와 같기 때문입니다.