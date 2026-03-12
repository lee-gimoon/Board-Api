package com.board.api.domain.member.controller;
// 이제 이 DTO를 사용하여 요청을 처리할 컨트롤러를 작성합니다.

import com.board.api.domain.member.dto.MemberLoginRequest;
import com.board.api.domain.member.dto.MemberSignupRequest;
import com.board.api.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * [핵심 포인트]
 * 1. @RestController: JSON 형태로 데이터를 주고받는 API 서버임을 선언합니다.
 * 2. @RequestMapping: 이 컨트롤러의 모든 주소는 "/api/members"로 시작합니다.
 */

@RestController
@RequestMapping("/api/members") // 공통 주소
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 API
     * POST http://localhost:8080/api/members/signup
     */
    @PostMapping("/signup") // POST 방식 전용 상세 주소
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

    /**
     * [추가된 로그인 API]
     * POST http://localhost:8080/api/members/login
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MemberLoginRequest request) {
        // 1. 서비스에게 로그인 검증 및 토큰 발급을 맡깁니다.
        String token = memberService.login(request.getEmail(), request.getPassword());

        // 2. 발급받은 토큰(긴 문자열)을 클라이언트(브라우저)에게 200 OK와 함께 돌려줍니다.
        return ResponseEntity.ok(token);
    }

    /**
     * 토큰 인증 테스트용 API (회원 목록 조회)
     * GET http://localhost:8080/api/members
     */
    @GetMapping
    public ResponseEntity<String> getMembers() {
        return ResponseEntity.ok("토큰 인증 성공! 회원 목록을 조회할 수 있습니다.");
    }

}//class
