package com.board.api.domain.member.dto;

// 이 DTO 클래스는 사용자가 로그인할 때 입력하는 이메일과 비밀번호를 담아서 컨트롤러로 전달해 주는 전용 바구니 역할을 합니다.

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [핵심 포인트]
 * @Getter: 필드 값을 꺼내 쓸 수 있게 해줍니다. (Jackson 라이브러리가 JSON을 변환할 때 필요함)
 * @NoArgsConstructor: 기본 생성자를 만들어줍니다. (스프링이 JSON 데이터를 이 객체로 조립할 때 꼭 필요함)
 */

@Getter
@NoArgsConstructor
public class MemberLoginRequest {

    private String email;
    private String password;

}