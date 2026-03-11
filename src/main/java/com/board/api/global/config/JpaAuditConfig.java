package com.board.api.global.config;
// [중요] 마지막 설정: JPA Auditing 활성화
// 위의 @CreatedDate 등이 실제로 작동하려면, 스프링 부트에게 "JPA 감시 기능을 켜줘!"라고 말해야 합니다.

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing // 이 어노테이션이 있어야 BaseTimeEntity의 시간 기능이 작동합니다!
public class JpaAuditConfig {

}
