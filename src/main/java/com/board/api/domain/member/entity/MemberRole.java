package com.board.api.domain.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 자바에서 등급이나 상태처럼 정해진 선택지를 관리할 때는 class 대신 enum을 사용합니다.
@Getter
@RequiredArgsConstructor // 필드를 포함한 생성자를 자동으로 만들어줍니다.
public enum MemberRole {

    // 1. 등급 정의
    // (Key: 시큐리티가 인식할 이름, Title: 우리가 화면에 보여줄 이름)
    USER("ROLE_USER", "일반 사용자"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String title;
}