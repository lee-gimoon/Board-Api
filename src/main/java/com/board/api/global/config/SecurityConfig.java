package com.board.api.global.config;
// 작성하신 SecurityConfig 파일이 아예 존재하지 않더라도, 프로젝트에 spring-boot-starter-security 라이브러리를 추가하고 서버를 실행하는 순간
// 스프링 부트의 **'자동 설정(Auto-Configuration) 마법'**이 발동하여 아주 깐깐한 기본 보안 검색대를 자동으로 세워버립니다.
// 즉, build.gradle에 spring-boot-starter-security를 추가하는 행위는 단순히 라이브러리를 가져오는 것을 넘어,
// **"내 애플리케이션의 모든 입구에 스프링 시큐리티 보안 요원을 배치하겠다"**는 선언과 같습니다.

// 프로젝트의 '보안 검색대' 역할을 할 ** 커스텀 SecurityConfig**를 설정할 차례입니다.
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

@Configuration // 스프링 부트에게 "이 클래스는 단순한 코드가 아니라, 앱이 켜질 때 읽어야 하는 설정(도면) 파일이야"라고 알려줍니다.
@EnableWebSecurity // "스프링 시큐리티의 기본 기능을 활성화하되, 내가 여기서 작성한 규칙들을 덮어씌워 줘"라는 스위치입니다.
@RequiredArgsConstructor // 롬복(Lombok) 기능으로, final이 붙은 필드(jwtAuthenticationFilter)를 자동으로 연결(주입)해 주는 생성자를 몰래 만들어줍니다.
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // 우리가 만든 문지기 주입.
    // 서버가 켜질 때 (Startup) 스프링 부트가 시작되면서 @Configuration이 붙은 클래스들을 샅샅이 찾습니다.
    // SecurityConfig를 발견하면, 그 안에 있는 @Bean 메서드(filterChain)를 실행합니다.
    // 이 메서드가 리턴한 SecurityFilterChain(완성된 보안 검색대)을 스프링의 **'객체 창고(Application Context)'**에 소중히 보관해둡니다.
    @Bean // 메서드 위에 붙어서 "이 메서드가 만들어낸 결과물(SecurityFilterChain)을 스프링 네가 창고에 보관하고 관리해!"라는 뜻입니다.
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // HttpSecurity http: 보안 규칙을 조립할 수 있는 블록(빌더)입니다. 이 http 객체에 .명령어().명령어() 형태로 체인처럼 엮어서 규칙을 완성합니다.
        http
                // 1. REST API 서버는 상태를 저장하지 않으므로 CSRF 방어를 끕니다.
                // AbstractHttpConfigurer::disable은 최신 스프링 시큐리티에서 기본 기능을 끌 때 사용하는 깔끔한 문법입니다.
                .csrf(AbstractHttpConfigurer::disable) // CSRF 방어 끄기: 세션/쿠키 기반일 때 필요한 방어막입니다. 우리는 토큰을 쓸 거라 필요 없습니다.
                // STATELESS (무상태성): 서버가 사용자를 기억(세션)하지 않겠다고 선언합니다. 매 요청마다 사용자가 직접 토큰을 들고 와서 증명해야 합니다.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션을 사용하지 않겠다고 명시
                )

                // 2. 시큐리티가 기본으로 띄우는 못생긴 HTML 로그인 창을 끕니다. (우리는 JSON으로 로그인할 거니까요!)
                .formLogin(AbstractHttpConfigurer::disable)

                // 3. HTTP Basic 인증(브라우저가 띄우는 기본 알림창 인증)도 끕니다.
                .httpBasic(AbstractHttpConfigurer::disable)

                // 4. 경로별 접근 권한 설정 (여기가 가장 중요합니다!)
                // 람다식(auth -> auth.결정(...))을 사용해 URL 주소 매칭과 권한을 세팅합니다. 위에서부터 아래로 순서대로 적용됩니다.
                .authorizeHttpRequests(auth -> auth
                        // permitAll(): 로비 같은 곳입니다. 회원가입/로그인은 신분증(토큰)이 없는 사람도 들어와야 하니 프리패스를 줍니다. 개발용 API 명세서(Swagger)도 열어둡니다.
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
                // 커스텀 필터(jwtAuthenticationFilter (내가 직접 로직을 창조함)) 끼워 넣기
                // addFilterBefore(A, B) -> B 필터가 작동하기 전에 A 필터를 먼저 실행하라는 명령어입니다.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                // 원래 순서: 시큐리티는 전통적인 "아이디/비번 폼 로그인" 방식인 UsernamePasswordAuthenticationFilter를 통해 인증을 하려고 기다립니다.
                // 우리의 전략: 하지만 우리는 폼 로그인을 쓰지 않죠? 그래서 그 필터가 작동하기 전에(addFilterBefore) 우리 커스텀 필터가 먼저 요청을 낚아채서
                // "이 사람 JWT 토큰 가져왔어! 내가 검증 끝냈으니까 통과시켜줘!"라고 선수(Authentication)를 치는 것입니다.
                // 결과: 우리 필터에서 인증이 완료되면, 뒤에 서 있는 기본 필터들은 "어? 이미 인증이 된 사용네?" 하고 그대로 통과시켜 주게 됩니다.

        return http.build(); // http.build() 메서드의 리턴 타입: SecurityFilterChain (완성된 검색대)
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


