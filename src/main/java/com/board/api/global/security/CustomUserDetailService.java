package com.board.api.global.security;

// DB 조회 요원 만들기 (CustomUserDetailsService)
// 사용자가 로그인하려고 이메일을 입력했을 때, 이 클래스가 출동해서 DB를 뒤져옵니다.
// 필터에서 토큰을 까보고 "음, 이메일이 test@abc.com이군." 하고 알아낸 뒤,
// **"어이, 통역사! 이 이메일 가진 사람 진짜 우리 회원 맞는지 DB 뒤져서 공식 신분증 좀 만들어와봐!"**라고 명령할 때 이 클래스가 호출됩니다.
// 이 클래스는 토큰에서 나온 이메일을 들고 DB로 달려가 진짜 회원인지 확인한 뒤, 시큐리티 전용 신분증(UserDetails)으로 예쁘게 포장해서 돌려주는 역할을 완벽하게 수행하고 있습니다.

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

@Service // 스프링(Spring)에게 "이 클래스는 비즈니스 로직을 담당하는 객체니까 네가 메모리에 띄우고 관리해 줘"라고 등록하는 마크입니다.
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // loadUserByUsername(String email): 시큐리티가 "유저 아이디 줄 테니까 정보 찾아와"라고 할 때 무조건 실행되는 공식 메서드입니다.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { // throws는 "나(메서드)는 이 에러를 직접 처리하지 않고, 나를 부른 녀석에게 책임을 넘기겠다!"라는 선언입니다.
        // 1. 우리가 만든 MemberRepository를 써서 DB에서 이메일로 회원을 찾습니다.
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 회원이 없습니다: " + email));

        // 2. DB에서 찾은 회원(Member)을 위에서 만든 신분증(PrincipalDetails)에 담아서 시큐리티에게 넘겨줍니다!
        return new PrincipalDetails(member);
    }

}//class

// 이 클래스를 왜 만든 거죠? (Why)
// 스프링 시큐리티는 굉장히 훌륭한 보안 요원이지만, 안타깝게도 우리가 만든 Member 엔티티나 MemberRepository의 존재를 전혀 모릅니다.
// 시큐리티는 오직 자기만의 공식 규격인 UserDetails(신분증)와 UserDetailsService(신분증 발급처)만 이해할 수 있거든요.

// 학습 포인트: 왜 이렇게 번거롭게 할까요?
// 스프링 시큐리티는 전 세계 수많은 기업이 씁니다. 어떤 회사는 아이디로 '사번'을 쓰고, 어떤 회사는 '전화번호'를 씁니다.
// 그래서 시큐리티는 **"너희가 DB에서 어떻게 찾든(UserDetailsService) 상관 안 할 테니,
// 나한테 줄 때만 내가 읽을 수 있는 규격(UserDetails)으로 포장해서 줘!"**라고 설계해 둔 것입니다. 이것이 바로 객체지향의 다형성과 인터페이스의 힘입니다.