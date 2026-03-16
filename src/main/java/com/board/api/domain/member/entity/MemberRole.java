package com.board.api.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 자바에서 등급이나 상태처럼 정해진 선택지를 관리할 때는 class 대신 enum을 사용합니다.
// 작성하신 MemberRole 클래스가 컴파일되면, 메모리에는 MemberRole 타입의 객체 딱 2개가 생성됩니다.
// 이 객체들은 프로그램이 켜질 때 딱 한 번 만들어지고, 프로그램이 꺼질 때까지 **단 하나씩만 존재(싱글톤)**합니다.
// 그래서 우리가 코드 어디서든 MemberRole.USER라고 부르면, 항상 메모리의 똑같은 곳에 있는 그 객체를 가리키게 됩니다.
@Getter
@RequiredArgsConstructor // 필드를 포함한 생성자를 자동으로 만들어줍니다.
public enum MemberRole {

    // USER("ROLE_USER", "일반 사용자")라는 문법은 사실 **"MemberRole 객체를 하나 만들 건데,
    // 첫 번째 칸(key)에는 'ROLE_USER'를 넣고 두 번째 칸(title)에는 '일반 사용자'를 넣어줘!"**라는 생성자 호출입니다.
    // 이렇게 만들어두면 나중에 자바 코드 어디서든 MemberRole.USER.getKey()를 호출하면 "ROLE_USER"가 튀어나오게 됩니다. 시큐리티에게 권한을 알려줄 때 아주 유용하죠.
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}

// 자바의 원래 모습(순수 문법)으로 길게 풀어쓰면 아래와 같습니다.
//public class MemberRole { // enum은 사실 특수한 형태의 class입니다.
//
//    // 1. 고정된 객체들을 미리 만들어 둡니다. (이것이 USER와 ADMIN의 정체입니다)
//    public static final MemberRole USER = new MemberRole("ROLE_USER", "일반 사용자");
//    public static final MemberRole ADMIN = new MemberRole("ROLE_ADMIN", "관리자");
//
//    // 2. 그 객체들이 가질 정보(상태)를 담을 주머니(필드)를 만듭니다.
//    private final String key;
//    private final String title;
//
//    // 3. 주머니에 값을 넣을 수 있도록 생성자를 만듭니다. (@RequiredArgsConstructor가 해준 일)
//    public MemberRole(String key, String title) {
//        this.key = key;
//        this.title = title;
//    }
//
//    // 4. 주머니에서 값을 꺼낼 수 있는 메서드를 만듭니다. (@Getter가 해준 일)
//    public String getKey() { return key; }
//    public String getTitle() { return title; }
//}