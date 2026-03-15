package com.board.api.global.config;
// JPA Auditing 활성화
// @CreatedDate 등이 실제로 작동하려면, 스프링 부트에게 "JPA 감시 기능을 켜줘!"라고 말해야 합니다.

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

// 전원 스위치 켜기: JpaAuditConfig
// 가장 먼저, 시스템 전체에 "시간 기록 기능을 작동시켜라!"라고 명령을 내리는 전원 스위치입니다.
// 설정 클래스는 "JPA야, 너 이런 마법 기능을 좀 써라"라고 지시하는 곳입니다.
@Configuration // @Configuration: 스프링 서버가 켜질 때 이 클래스를 찾아내서 환경 설정 파일로 읽어 들입니다.
@EnableJpaAuditing // 핵심 스위치입니다. 스프링 내부의 '감시자(Auditor)'를 깨워서, 앞으로 DB에 데이터가 들어오고 나갈 때 시간을 잴 준비를 시킵니다.
public class JpaAuditConfig {

}

// 스프링 부트의 메인 클래스(@SpringBootApplication)가 실행되면, 스프링은 프로젝트 내의 모든 패키지를 뒤져서 특정 어노테이션이 붙은 클래스들을 찾습니다.
// 스프링이 프로젝트를 뒤질 때(Component Scan), 단순히 아무 클래스나 다 가져오는 게 아니라 **"이건 스프링이 관리해야 할 부품이야!"**라고 표시된 특정 **'배지(Badge)'**를 찾습니다.
// 이 배지들을 통칭해서 스테레오타입(Stereotype) 어노테이션이라고 부르는데, 어떤 것들이 있는지 정리해 드릴게요.

// 1. 모든 배지의 조상: @Component
// 가장 기본이 되는 어노테이션입니다. 아래에 설명할 모든 어노테이션은 사실 내부에 이 @Component를 품고 있습니다.
// 스프링은 클래스 위에 @Component가 붙어 있으면 "오, 이건 내가 관리할 **빈(Bean)**이구나" 하고 메모리에 올립니다.

// 2. 역할별 전문 배지 (Stereotypes)
// 개발자가 코드를 읽기 편하게, 그리고 스프링이 용도에 맞게 특수 기능을 더할 수 있게 @Component를 구체화한 것들입니다.
// @Controller: 웹 요청을 받는 관문.  주로 HTML 뷰를 반환할 때 사용합니다.
// @RestController:	REST API 전용 관문.  @Controller + @ResponseBody.JSON 데이터를 반환합니다.
// @Service: 비즈니스 로직 수행.  특별한 기능보다는 "여기가 핵심 로직이 있는 곳"임을 명시합니다.
// @Repository: 데이터베이스 접근.  DB 예외를 스프링 공통 예외로 변환해주는 기능이 추가됩니다.
// @Configuration: 설정 정보.  클래스 내의 @Bean 메서드들을 실행해 수동으로 빈을 등록합니다.

// 3. 그 외에 스캔 대상이 되는 것들
// 단순히 부품 등록 외에도, 시스템 전체에 영향을 주는 특수한 어노테이션들도 스캔 대상입니다.
// @ControllerAdvice / @RestControllerAdvice: 모든 컨트롤러에서 발생하는 예외를 한곳에서 처리하기 위해 사용합니다.
// @Aspect: AOP를 구현할 때 사용하며, 공통 관심사(로그, 보안 등)를 정의한 클래스를 찾을 때 사용합니다.