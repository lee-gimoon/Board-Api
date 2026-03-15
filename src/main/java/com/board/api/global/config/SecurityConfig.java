package com.board.api.global.config;

// 프로젝트의 '보안 검색대' 역할을 할 **SecurityConfig**를 설정할 차례입니다.
// 여기서는 "어떤 주소는 그냥 통과시켜 주고, 어떤 주소는 신분증(토큰)을 검사해라"라는 규칙을 정하고,
// 비밀번호를 안전하게 숨기는 도구를 빈(Bean)으로 등록합니다.

// 문단속 및 비밀번호 암호화 설정 (SecurityConfig)
// 우리는 화면(HTML)을 반환하는 옛날 방식의 웹이 아니라, 데이터(JSON)만 주고받는 REST API 서버를 만들고 있습니다.
// 따라서 시큐리티가 기본으로 제공하는 로그인 화면이나 세션 설정 등은 끄고, API에 맞는 설정으로 세팅해 주어야 합니다.

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.board.api.global.security.JwtAuthenticationFilter; // [추가]
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // [추가]
import lombok.RequiredArgsConstructor; // [추가]

/**
 * [핵심 포인트]
 * 1. @Configuration: 스프링이 켜질 때 이 클래스의 설정을 읽어갑니다.
 * 2. @EnableWebSecurity: 스프링 시큐리티의 모든 방어막(필터 체인)을 활성화합니다.
 */

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor // [추가] 필터를 주입받기 위해 필요합니다!
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // [추가] 우리가 만든 문지기 주입

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. REST API 서버는 상태를 저장하지 않으므로 CSRF 방어를 끕니다.
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // [추가] 세션을 사용하지 않겠다고 명시
                )

                // 2. 시큐리티가 기본으로 띄우는 못생긴 HTML 로그인 창을 끕니다. (우리는 JSON으로 로그인할 거니까요!)
                .formLogin(AbstractHttpConfigurer::disable)

                // 3. HTTP Basic 인증(브라우저가 띄우는 기본 알림창 인증)도 끕니다.
                .httpBasic(AbstractHttpConfigurer::disable)

                // 4. 경로별 접근 권한 설정 (여기가 가장 중요합니다!)
                .authorizeHttpRequests(auth -> auth
                        // 회원가입과 로그인 API는 신분증 없이도(permitAll) 누구나 접근할 수 있어야 합니다.
                        .requestMatchers("/api/members/signup", "/api/members/login", "/api/members").permitAll()

                        // Swagger 관련 주소들을 모두 허용(permitAll) 목록에 추가합니다.
                        .requestMatchers(
                                "/v3/api-docs/**",    // Swagger가 만드는 API 명세 데이터(JSON)
                                "/swagger-ui/**",     // Swagger UI 화면(HTML, JS, CSS)
                                "/swagger-ui.html"    // Swagger 접속용 단축 주소
                        ).permitAll()

                        // 그 외의 모든 요청(게시글 작성 등)은 반드시 인증(authenticated)을 거쳐야 합니다.
                        .anyRequest().authenticated()
                )
                // [핵심 추가 포인트]
                // 시큐리티가 기본으로 쓰는 폼 로그인 문지기(UsernamePasswordAuthenticationFilter)가
                // 검사하기 '전(Before)'에 우리가 만든 JWT 문지기를 먼저 거치도록 설정합니다.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 비밀번호를 안전하게 암호화해주는 도구입니다.
     * MemberService에서 passwordEncoder.encode(password) 할 때 이 녀석이 사용됩니다.
     */
    @Bean // 스프링에게 "이 메서드가 반환하는 객체를 네가 관리하는 **빈(Bean)**으로 등록해줘"라고 명령하는 것입니다.
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // PasswordEncoder	인터페이스 (추상화): "이 기계는 '비밀번호 처리기'라는 규격을 따라야 해"
    // BCryptPasswordEncoder  실제 구현체 (알고리즘): "실제 기계는 가장 튼튼한 'BCrypt 모델'로 설치해"
}


