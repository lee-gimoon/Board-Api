package com.board.api.domain.member.controller;
// 이제 이 DTO를 사용하여 요청을 처리할 컨트롤러를 작성합니다.

import com.board.api.domain.member.dto.MemberLoginRequest;
import com.board.api.domain.member.dto.MemberResponse;
import com.board.api.domain.member.dto.MemberSignupRequest;
import com.board.api.domain.member.service.MemberService;
import com.board.api.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * [핵심 포인트]
 * 1. @RestController: JSON 형태로 데이터를 주고받는 API 서버임을 선언합니다.
 * 2. @RequestMapping: 이 컨트롤러의 모든 주소는 "/api/members"로 시작합니다.
 */

@RestController
@RequestMapping("/api/members") // 공통 주소. @Target({ElementType.TYPE,ElementType.METHOD})
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 API
     * POST http://localhost:8080/api/members/signup
     */
    @PostMapping("/signup") // POST 방식 전용 상세 주소. @Target({ElementType.METHOD})
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
     * 토큰 인증 테스트용 API (회원 정보 연동)
     */
    @Operation(summary = "내 정보 조회") // 설명은 남겨두는 게 좋아요!
    @GetMapping("/me") //  @Target({ElementType.METHOD})
    public ResponseEntity<String> getMembers(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        // 여기에 로그를 찍어서 실제로 뭐가 들어오는지 확인합니다.
        System.out.println("컨트롤러에 전달된 인증 정보: " + principalDetails);

        // 1. PrincipalDetails가 null인지 체크 (혹시 모를 에러 방지)
        if (principalDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증 정보가 없습니다.");
        }

        // 2. 로그인한 유저의 실제 정보 꺼내기
        String email = principalDetails.getUsername();
        String nickname = principalDetails.getName().getNickname();

        // 3. 응답 보내기
        return ResponseEntity.ok("인증 성공! 반갑습니다, " + nickname + "님. (" + email + ")");
    }

    @GetMapping // 메서드에 아무것도 적지 않았다는 것은 **"이 클래스의 대표 주소로 GET 요청이 들어오면 내가 처리하겠다"**는 뜻입니다.
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        // 서비스에게 "회원들 목록 가져와서 Response DTO로 바꿔줘"라고 시킵니다.
        List<MemberResponse> members = memberService.findAll();
        return ResponseEntity.ok(members);
    }

}//class
