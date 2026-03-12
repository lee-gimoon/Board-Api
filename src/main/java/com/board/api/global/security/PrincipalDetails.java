package com.board.api.global.security;

// 스프링 시큐리티는 굉장히 고집이 셉니다. 우리가 정성껏 만든 Member 객체를 들이밀어도 "난 내가 정한 규격이 아니면 안 받아줘!"라고 하거든요.
// 그래서 시큐리티가 이해할 수 있는 **'전용 신분증(UserDetails)'**과 **'DB 조회 요원(UserDetailsService)'**을 만들어주어야 합니다.

// 시큐리티 전용 신분증 만들기 (PrincipalDetails)
// 우리 시스템의 Member 엔티티를 시큐리티가 읽을 수 있도록 포장해 주는 클래스입니다.

import com.board.api.domain.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * [핵심 포인트]
 * 1. UserDetails 인터페이스를 구현(implements)해야 시큐리티가 신분증으로 인정해줍니다.
 * 2. 내부에 우리가 만든 Member 객체를 품고(컴포지션) 있습니다.
 */
public class PrincipalDetails implements UserDetails {
    private final Member member;

    public PrincipalDetails(Member member) {
        this.member = member;
    }

    // 나중에 컨트롤러 등에서 실제 Member 객체를 꺼내 쓰고 싶을 때 사용합니다.
    public Member getName() {
        return member;
    }

    // 1. 회원의 권한(Role)을 리턴합니다. (일단 모두 일반 유저로 설정)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    // 2. 회원의 비밀번호를 리턴합니다.
    @Override
    public String getPassword() {
        return member.getPassword();
    }

    // 3. 회원의 아이디(우리는 이메일)를 리턴합니다.
    @Override
    public String getUsername() {
        return member.getEmail();
    }

    // -- 아래 4개의 메서드는 계정의 상태를 나타냅니다. (지금은 모두 사용 가능(true)으로 설정 --

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정이 만료되지 않았는가?
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정이 잠기지 않았는가?
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 비밀번호가 만료되지 않았는가?
    }

    @Override
    public boolean isEnabled() {
        return true; // 계정이 활성화 상태인가?
    }

}//class












