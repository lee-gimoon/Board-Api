package com.board.api.global.security;

// 문지기 배치하기 (JwtAuthenticationFilter.java)
// 이 필터(문지기)는 사용자의 모든 요청이 컨트롤러(안내데스크)에 도달하기 전에 미리 가로채서 **"입장권(헤더의 토큰) 보여주세요.
// 위조된 건 아닌지, 만료되진 않았는지 검사하겠습니다."**라고 확인하는 역할을 합니다. (입장권 검사)

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * [핵심 포인트]
 * OncePerRequestFilter를 상속받으면, 사용자의 요청 한 번당 딱 한 번만 이 필터가 실행됩니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 요청의 헤더(Header)에서 토큰표 꺼내기
        String token = resolveToken(request);

        try {
            // 2. 토큰이 존재한다면 검사 시작!
            if (token != null) {
                // 토큰을 해독해서 이메일을 꺼냅니다. (위조/만료된 토큰이면 여기서 에러가 팍! 터집니다)
                String email = jwtProvider.getEmailFromToken(token);

                // 이메일이 정상적으로 나왔다면, DB에서 진짜 회원인지 '신분증'을 가져옵니다.
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // "이 사람은 통과!" 라고 시큐리티에게 알려주는 '인증 도장'을 찍습니다.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // 시큐리티의 'VIP 명부(SecurityContext)'에 이 사람을 등록합니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            // 여기에 에러를 찍어보세요!
            // 토큰 파싱 에러인지, NullPointerException인지 바로 알 수 있습니다.
            log.error("인증 실패: {}", e.getMessage());
            e.printStackTrace();
        }

        // 3. 검사가 끝났으니 다음 필터(혹은 컨트롤러)로 요청을 넘겨줍니다.
        filterChain.doFilter(request, response);
    }

    /**
     * HTTP 요청 헤더에서 토큰만 쏙 빼오는 유틸리티 메서드입니다.
     * 국제 표준 규칙: "Authorization: Bearer <토큰문자열>" 형태로 들어옵니다.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // 헤더에 값이 있고, "Bearer "로 시작하는지 확인
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " 글자(7칸)를 잘라내고 순수 토큰만 반환
        }
        return null;
    }
}