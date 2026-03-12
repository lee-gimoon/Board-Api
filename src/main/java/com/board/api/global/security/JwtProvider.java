package com.board.api.global.security;

// JWT란 무엇인가요? (놀이공원 자유이용권)
// 로그인에 성공한 사용자에게 서버가 발급해 주는 '디지털 자유이용권'입니다.
// Header: "이 티켓은 JWT 방식으로 만들어졌어."
// Payload (Claims): "이 티켓의 주인은 test@test.com이고, 오늘 밤 12시까지 유효해."
// Signature: "이 티켓은 우리 서버(board_api)가 발급한 진짜 티켓이 맞음! (위조 방지 도장)"

// 이 티켓을 만들어주는 기계인 **JwtProvider**를 만들어 봅시다.

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * [핵심 포인트]
 * @Component: 스프링이 이 클래스를 관리하도록 빈(Bean)으로 등록합니다.
 * 필요할 때마다 의존성 주입(@RequiredArgsConstructor)으로 불러다 쓸 수 있습니다.
 */

@Component
public class JwtProvider {
    // 1. 서버만의 비밀 도장 (절대 외부에 유출되면 안 됩니다!)
    // 주의: HS256 알고리즘을 쓰려면 키 길이가 최소 32글자 이상이어야 합니다.
    private final String secretKeyString = "my-super-secret-key-for-jwt-which-must-be-long-enough-12345";
    private final Key secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());

    // 2. 티켓 유효 시간 (예: 1시간 = 1000ms * 60초 * 60분)
    private final long tokenValidTime = 1000L * 60 * 60;

    /**
     * 로그인 성공 시 호출되어 JWT 티켓을 문자열로 만들어주는 메서드입니다.
     */
    public String createToken(String email) {
        // 티켓에 들어갈 내용(Claims)을 셋팅합니다. (여기서는 이메일만 넣습니다)
        Claims claims = Jwts.claims().setSubject(email);

        Date now = new Date();

        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now)  // 티켓 발행 시간
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // 티켓 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // 비밀 도장 쾅!
                .compact(); // 이 모든 걸 합쳐서 압축된 문자열로 만듦
    }

    /**
     * 나중에 사용자가 티켓을 가져왔을 때, 티켓에서 이메일을 다시 읽어내는 메서드입니다.
     */
    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // 위조된 티켓인지 우리 도장으로 먼저 확인
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // 아까 넣었던 이메일 꺼내기
    }
}//class

// JwtProvider는 철저하게 "토큰을 만들고, 까보는" 도구 모음집입니다.
// 가장 중요한 건 secretKey입니다. 만약 해커가 저 키를 알게 되면,
// 해커 컴퓨터에서 마음대로 티켓을 찍어내서 우리 서버로 들어올 수 있게 됩니다. (지금은 하드코딩 했지만, 실무에서는 보통 application.yml 같은 환경 설정 파일에 숨겨둡니다!)
