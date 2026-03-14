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
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 아무데서나 객체를 생성하지 못하게 막아 안전성을 높입니다.
@Table(name = "members") // DB에는 members라는 이름의 테이블로 생성됩니다.
public class Member extends BaseTimeEntity {

    @Id // 기본 키(Primary Key)임을 나타냅니다.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 번호를 DB가 자동으로 1, 2, 3... 채워줍니다.
    private Long id;

    @Column(nullable = false, unique = true) // 이메일은 필수고 중복될 수 없습니다.
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Builder // 빌더 패턴을 사용하여 객체 생성을 안전하고 가독성 좋게 만듭니다.
    public Member(String email, String password, String nickname) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
    }
}//class
