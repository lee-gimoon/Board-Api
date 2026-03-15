package com.board.api.global.common;
// 이 클래스는 모든 도메인(회원, 게시글 등)이 공통으로 상속받아 '생성 시간'과 '수정 시간'을 자동으로 관리하게 해줍니다.

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
// 시간 기록 도장 (설계도): BaseTimeEntity
// 이 클래스는 실제 DB 테이블로 만들어지는 것이 아니라, 다른 엔티티들이 상속받아서 쓸 수 있는 **'공통 필드(시간) 설계도'**입니다.


/**
 * [핵심 포인트]
 * 1. @MappedSuperclass: 이 클래스를 상속받는 클래스들에게 필드(createdAt, updatedAt)를 상속해줍니다.
 * 2. @EntityListeners: JPA에게 이 클래스에 감시(Auditing) 기능을 쓰겠다고 알립니다. (**"JPA 표준 규약에 따라 설정한다"**)
 */

@Getter
@MappedSuperclass // "나를 상속받는 자식 클래스(Member 등)에게 내 필드(createdAt, updatedAt)를 자기 것(컬럼)처럼 쓰게 해줄게!"라는 뜻입니다.
// @EntityListeners(AuditingEntityListener.class)는 전역적으로 켜진 아우디팅(Auditing) 기능을 "특정 엔티티에 실제로 연결하는 접점" 역할을 합니다.
@EntityListeners(AuditingEntityListener.class) // 엔티티가 DB에 저장되거나 수정되는 이벤트(변화)가 발생할 때, 괄호 안의 AuditingEntityListener라는 스프링 내부 요원에게 "네가 개입해서 시간을 채워 넣어!"라고 위임하는 것입니다.
public abstract class BaseTimeEntity {
    // @CreatedDate / @LastModifiedDate: 그 요원이 시간을 채워 넣을 구체적인 '빈칸'을 지정해 줍니다.
    @CreatedDate // 데이터가 처음 저장될 때 시간이 자동으로 입력됩니다.
    @Column(updatable = false) // 생성 시간은 수정되면 안 되므로 옵션을 줍니다.
    private LocalDateTime createdAt;

    @LastModifiedDate // 데이터가 수정될 때마다 시간이 자동으로 갱신됩니다.
    private LocalDateTime updatedAt;
}
