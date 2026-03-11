package com.board.api.domain.member.dto;
// 데이터를 담아 나르는 상자, DTO (Data Transfer Object)
// 왜 엔티티(Member)를 안 쓰고 DTO를 따로 만드나요?
// 엔티티는 DB와 직결된 매우 중요한 설계도입니다. 외부 사용자가 엔티티를 직접 건드리게 하면 보안상 위험하고, 불필요한 정보(예: ID, 생성 시간 등)까지 노출될 수 있기 때문입니다.

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberSignupRequest {
    private String email;
    private String password;
    private String nickname;
}
