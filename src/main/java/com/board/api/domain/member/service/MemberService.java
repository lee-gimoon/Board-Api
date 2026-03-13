package com.board.api.domain.member.service;
// 이제 프로젝트의 '총괄 매니저' 격인 서비스(Service) 계층을 만들 차례입니다.
// 리포지토리가 "DB에서 데이터를 가져와!"라는 단순 심부름을 한다면, 서비스는 "가져온 데이터를 검사해보고, 비밀번호도 암호화하고, 문제가 없으면 저장해!"라는 실제 비즈니스 로직을 담당합니다.

import com.board.api.domain.member.dto.MemberResponse;
import com.board.api.domain.member.entity.Member;
import com.board.api.domain.member.repository.MemberRepository;
import com.board.api.global.security.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * [핵심 포인트]
 * 1. @Service: 스프링이 이 클래스를 "비즈니스 로직 담당자"로 인식하게 합니다.
 * 2. @Transactional: 이 메서드 안의 작업들은 '하나의 작업 단위'로 묶입니다. (전부 성공하거나, 하나라도 실패하면 취소!)
 * 3. @RequiredArgsConstructor: 생성자를 통해 MemberRepository를 자동으로 주입(Injection)받습니다.
 */

@Service
@Transactional(readOnly = true) // 기본적으로 읽기 전용으로 설정 (성능 최적화)
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    // [추가됨] SecurityConfig에서 만든 암호화 도구를 스프링이 알아서 주입(Injection)해 줍니다.
    private final PasswordEncoder passwordEncoder;
    // [추가] 스프링이 알아서 주입해 줍니다.
    private final JwtProvider jwtProvider;

    // 회원가입 로직.
    @Transactional // 데이터를 저장해야 하므로 읽기 전용을 해제합니다.
    public Long signup(String email, String password, String nickname) {
        // 1. 이메일 중복 체크 (우리가 Repository에 만든 메서드 사용!)
        validateDuplicateMember(email);

        // 2. [수정됨] 사용자가 입력한 평문 비밀번호(예: "1234")를 복호화 불가능한 외계어로 암호화합니다.
        String encodedPassword = passwordEncoder.encode(password);

        // 3. 회원 객체 생성 및 저장
        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .build();

        return memberRepository.save(member).getId();
    }

    /**
     * [추가된 로그인 로직]
     * 이메일과 비밀번호를 확인한 뒤, 성공하면 JWT 토큰(문자열)을 반환합니다.
     */
    public String login(String email, String password) {
        // 1. DB에서 이메일로 회원을 찾습니다. 없으면 에러 발생!
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        // 2. 비밀번호가 일치하는지 확인합니다.
        // passwordEncoder.matches(입력받은 비밀번호, DB에 암호화되어 저장된 비밀번호)
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 이메일과 비밀번호가 모두 맞다면, 티켓 발급기를 돌려서 토큰을 만들어 줍니다!
        return jwtProvider.createToken(member.getEmail());
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream() // DB에서 모든 멤버를 꺼내서
                .map(MemberResponse::new)          // 하나씩 MemberResponse로 변환한 뒤
                .toList();                         // 리스트로 묶어서 반환합니다.
    }

    private void validateDuplicateMember(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
    }
}//class
