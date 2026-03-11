package com.board.api.domain.member.controller;
// 이제 이 DTO를 사용하여 요청을 처리할 컨트롤러를 작성합니다.

import com.board.api.domain.member.dto.MemberSignupRequest;
import com.board.api.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * [핵심 포인트]
 * 1. @RestController: JSON 형태로 데이터를 주고받는 API 서버임을 선언합니다.
 * 2. @RequestMapping: 이 컨트롤러의 모든 주소는 "/api/members"로 시작합니다.
 */

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 API
     * POST http://localhost:8080/api/members/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@RequestBody MemberSignupRequest request) {
        // 1. 서비스에 가입 요청을 보냄
        Long memberId = memberService.signup(
                request.getEmail(),
                request.getPassword(),
                request.getNickname()
        );

        // 2. 성공 시 생성된 회원 ID와 함께 200 OK 응답을 보냄
        return ResponseEntity.ok(memberId);
    }
}
