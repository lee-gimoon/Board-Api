package com.board.api.global.security;

// DB 조회 요원 만들기 (CustomUserDetailsService)
// 사용자가 로그인하려고 이메일을 입력했을 때, 이 클래스가 출동해서 DB를 뒤져옵니다.

import com.board.api.domain.member.entity.Member;
import com.board.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * [핵심 포인트]
 * UserDetailsService 인터페이스를 구현하면, 스프링 시큐리티가 로그인 요청을 받을 때
 * 자동으로 이 클래스의 loadUserByUsername 메서드를 실행합니다.
 */

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. 우리가 만든 MemberRepository를 써서 DB에서 이메일로 회원을 찾습니다.
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 회원이 없습니다: " + email));

        // 2. DB에서 찾은 회원(Member)을 위에서 만든 신분증(PrincipalDetails)에 담아서 시큐리티에게 넘겨줍니다!
        return new PrincipalDetails(member);
    }

}//class


// 학습 포인트: 왜 이렇게 번거롭게 할까요?
// 스프링 시큐리티는 전 세계 수많은 기업이 씁니다. 어떤 회사는 아이디로 '사번'을 쓰고, 어떤 회사는 '전화번호'를 씁니다.
// 그래서 시큐리티는 **"너희가 DB에서 어떻게 찾든(UserDetailsService) 상관 안 할 테니,
// 나한테 줄 때만 내가 읽을 수 있는 규격(UserDetails)으로 포장해서 줘!"**라고 설계해 둔 것입니다. 이것이 바로 객체지향의 다형성과 인터페이스의 힘입니다.