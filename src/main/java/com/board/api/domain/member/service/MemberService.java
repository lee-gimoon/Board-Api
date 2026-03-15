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

// @Service: "이 클래스는 비즈니스 로직을 담당하는 핵심 부품(Bean)이니까, 네가 직접 객체로 만들어서 네 주머니(스프링 컨테이너)에 넣고 관리해줘!"라고 말하는 것입니다
@Service
// 스프링이 @Transactional이 붙은 클래스를 빈으로 등록할 때, 진짜 객체(MemberService)를 그냥 내보내지 않고 **껍데기(Proxy)**를 씌워서 내보냅니다.
@Transactional(readOnly = true) // 스프링의 @Transactional은 클래스 레벨의 설정이 메서드 레벨로 자동 상속됩니다. 기본적으로 읽기 전용으로 설정 (성능 최적화).
@RequiredArgsConstructor // @RequiredArgsConstructor는 롬복이 제공하는 기능으로, final이 붙은 필수 부품들만 모아 생성자를 자동으로 만들어줍니다.
// 스프링의 규칙: "어떤 클래스(Bean)에 생성자가 딱 1개만 있다면, 굳이 @Autowired 어노테이션을 안 붙여도 내가 알아서 그 생성자를 호출해서 부품을 쫙 꽂아줄게!"
public class MemberService {
    // 필드 주입의 한계 (@Autowired를 쓰지 않고 @RequiredArgsConstructor쓰는 이유)
    // 필드 주입은 리플렉션으로 나중에 값을 쑤셔 넣어야 하므로, 자바의 강력한 잠금장치인 final 키워드를 쓸 수 없습니다.
    // 누군가 악의적이거나 실수로 실행 중에 부품을 바꿔치기할 위험이 열려 있는 것이죠.
    private final MemberRepository memberRepository;
    // [추가됨] SecurityConfig에서 만든 암호화 도구를 스프링이 알아서 주입(Injection)해 줍니다.
    private final PasswordEncoder passwordEncoder;
    // [추가] 스프링이 알아서 주입해 줍니다.
    private final JwtProvider jwtProvider;

    // 회원가입 로직.
    // 스프링에서 @Transactional이 붙은 메서드가 있다면, 스프링은 그 객체를 그대로 사용하는 게 아니라 겉을 감싸는 **'가짜 객체(Proxy)'**를 만들어냅니다.
    @Transactional // 데이터를 저장해야 하므로 읽기 전용을 해제합니다. (메서드 위에 그냥 @Transactional을 다시 붙여서 **"이 메서드는 읽기 전용이 아니야!"**라고 덮어쓰기(Override)를 해줘야 합니다.)
    public Long signup(String email, String password, String nickname) {
        // 1. 이메일 중복 체크 (우리가 Repository에 만든 메서드 사용! .existsByEmail)
        validateDuplicateMember(email);

        // 2. [수정됨] 사용자가 입력한 평문 비밀번호(예: "1234")를 복호화 불가능한 외계어로 암호화합니다.
        String encodedPassword = passwordEncoder.encode(password);

        // 3. 회원 객체 생성 및 저장 (엔티티는 서비스 안에서만 생성되고 다뤄지는 게 안전합니다.)
        Member member = Member.builder()
                .email(email)
                .password(encodedPassword)
                .nickname(nickname)
                .build();

        // 우리가 memberRepository.save()를 호출하면, 실제로는 스프링이 만든 레포지토리 프록시가 그 요청을 받아서 실제 DB에 저장하는 복잡한 JPA 코드(SimpleJpaRepository)를 대신 실행해 줍니다.
        return memberRepository.save(member).getId();
        // memberRepository: 프록시 (스프링이 만든 대리인). "프록시는 진짜 객체와 똑같이 생겼지만, 겉을 감싸서 부가 기능을 대신 수행해 주는 인위적인 대리 객체입니다." (AOP 개념)
        // .save(): 프록시의 기능 (DB 저장 로직 실행)
        // member (반환된 결과): 순수 객체 (데이터가 담긴 엔티티) (save() => DB에 저장된 후, ID값이 채워진 Member 객체를 반환합니다.)
        // .getId(): 순수 메서드 호출 (필드 값 읽기)
        // 한 가지 재밌는 사실은, memberRepository.save(member)를 호출하기 전의 member 객체는 id가 null인 상태입니다.
        // 하지만 save()라는 프록시의 마법을 거쳐서 돌아온 객체는 DB가 생성해 준 id를 아주 예쁘게 가지고 있습니다.
        // 그래서 바로 뒤에 .getId()를 붙여도 null이 아닌 실제 숫자를 받을 수 있게 되는 것입니다.
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

    public List<MemberResponse> findAll() {
        return memberRepository.findAll().stream() // DB에서 모든 멤버를 꺼내서
                .map(MemberResponse::new)          // 하나씩 MemberResponse로 변환한 뒤
                .toList();                         // 리스트로 묶어서 반환합니다.
    }

    private void validateDuplicateMember(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
            // 1. throw 문이 실행되는 순간, 그 아래에 있는 코드들은 전혀 실행되지 않고 메서드가 즉시 종료됩니다.
            // 2. 이 메서드를 호출했던 상위 메서드(보통 Service나 Controller)로 예외 객체가 던져집니다.
            // 3. 어디선가 try-catch로 이 예외를 잡으면(catch) 프로그램은 계속 돌아갑니다.
            // 아무도 잡지 않으면? 결국 스프링 부트의 기본 에러 처리기까지 올라가서 사용자에게 500 Internal Server Error를 보여주며 멈추게 됩니다.
        }
    }
}//class

// 참고로 "메서드 안에서 다른 메서드를 부르는 것은 **this**를 호출하는 것이고,
// this는 프록시가 아니기 때문에 아무리 @Transactional이 붙어 있어도 트랜잭션 기능이 작동하지 않습니다."
// public void anyMethod() {
//    // 같은 클래스 안에서 signup을 호출하면?
//    signup("test@test.com", "1234", "nick"); // 이 경우 트랜잭션 적용 안됨.
//}