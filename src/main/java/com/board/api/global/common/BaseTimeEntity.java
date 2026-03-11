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

/**
 * [핵심 포인트]
 * 1. @MappedSuperclass: 이 클래스를 상속받는 클래스들에게 필드(createdAt, updatedAt)를 상속해줍니다.
 * 2. @EntityListeners: JPA에게 이 클래스에 감시(Auditing) 기능을 쓰겠다고 알립니다. (**"JPA 표준 규약에 따라 설정한다"**)
 */
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class) // AuditingEntityListener (구현체): "그 일은 내가 할게! 내가 실제로 컴퓨터 시계를 보고 시간을 계산해서 빈칸을 채우는 코드를 다 짜놨어." (Spring Data JPA가 미리 만들어둔 클래스)
public abstract class BaseTimeEntity {
    @CreatedDate // 데이터가 처음 저장될 때 시간이 자동으로 입력됩니다.
    @Column(updatable = false) // 생성 시간은 수정되면 안 되므로 옵션을 줍니다.
    private LocalDateTime createdAt;

    @LastModifiedDate // 데이터가 수정될 때마다 시간이 자동으로 갱신됩니다.
    private LocalDateTime updatedAt;
}
