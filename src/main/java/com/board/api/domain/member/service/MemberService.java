package com.board.api.domain.member.service;
// 이제 프로젝트의 '총괄 매니저' 격인 서비스(Service) 계층을 만들 차례입니다.
// 리포지토리가 "DB에서 데이터를 가져와!"라는 단순 심부름을 한다면, 서비스는 "가져온 데이터를 검사해보고, 비밀번호도 암호화하고, 문제가 없으면 저장해!"라는 실제 비즈니스 로직을 담당합니다.

import com.board.api.domain.member.entity.Member;
import com.board.api.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    // 회원가입 로직.
    @Transactional // 데이터를 저장해야 하므로 읽기 전용을 해제합니다.
    public Long signup(String email, String password, String nickname) {
        // 1. 이메일 중복 체크 (우리가 Repository에 만든 메서드 사용!)
        validateDuplicateMember(email);

        // 2. 비밀번호 암호화 (지금은 뼈대만 만들고, 나중에 Security 설정을 추가할 거예요)
        String encodedPassword = password; // TODO: PasswordEncoder 사용 예정

        // 3. 회원 객체 생성 및 저장
        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .build();

        return memberRepository.save(member).getId();
    }

    private void validateDuplicateMember(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }
    }
}//class
