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

@RestController // @Controller + @ResponseBody. (@ResponseBody => **"내 메서드가 반환하는 값을 HTML 뷰(View)를 찾는 데 쓰지 말고, 바로 HTTP 응답 본문(Body)에 담아줘!"**라고 스프링에게 요청하는 신호)
@RequestMapping("/api/members") // 공통 주소. @Target({ElementType.TYPE,ElementType.METHOD})
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    /**
     * 회원가입 API
     * POST http://localhost:8080/api/members/signup
     */
    @PostMapping("/signup") // POST 방식 전용 상세 주소. @Target({ElementType.METHOD})
    // ResponseEntity는 스프링에서 제공하는 클래스로, HTTP 응답(Response) 전체를 나타내는 객체입니다.
    // <Long> (제네릭): 이건 "응답 객체 안의 실제 알맹이(Body) 데이터는 Long 타입이야"라고 구체적으로 명시하는 것입니다.
    // 회원가입이 끝나고 memberId를 돌려주기로 했으니, ID의 타입인 Long을 써준 것입니다.
    public ResponseEntity<Long> signup(@RequestBody MemberSignupRequest request) { // @RequestBody는 이름 그대로 **"HTTP 요청 본문(Body)에 담긴 내용을 자바 객체로 변환해서 넣어달라"**고 스프링에게 부탁하는 어노테이션입니다.
        // 1. 서비스에 가입 요청을 보냄
        Long memberId = memberService.signup(
                request.getEmail(),
                request.getPassword(),
                request.getNickname()
        );

        // 2. 성공 시 생성된 회원 ID와 함께 200 OK 응답을 보냄
        // ResponseEntity.ok()는 **"HTTP 상태 코드 200 OK"**를 의미하는 정적(static) 메서드입니다.
        // 뒤에 따라오는 (memberId)는 이 응답의 **본문(Body)**에 해당 데이터를 실어 보내겠다는 뜻입니다.
        return ResponseEntity.ok(memberId);
        // ResponseEntity.ok(memberId)를 리턴하면: ID를 품고 있는 '성공 응답 객체'를 새로 만들어서 반환하는 것입니다.
        // 브라우저가 응답을 받자마자 **"상태 코드: 200"**을 보고 "아, 회원가입이 성공했구나!"라고 먼저 판단합니다.
        // 그다음에 박스를 열어(Body를 확인해) "생성된 ID는 1이구나"라고 데이터를 꺼내 씁니다.
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
