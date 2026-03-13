package com.board.api.domain.member.dto;

import com.board.api.domain.member.entity.Member;
import lombok.Getter;

@Getter
public class MemberResponse {
    private final Long id;
    private final String email;
    private final String nickname;

    // 엔티티를 DTO로 변환해주는 생성자입니다.
    public MemberResponse(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.nickname = member.getNickname();
    }
}