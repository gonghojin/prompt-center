# 🏗️ 백엔드 시스템 아키텍처

## 기술 스택
- **언어**: Java 17
- **프레임워크**: Spring Boot
- **라이브러리**:
  - Lombok
  - JPA
  - Spring Security
  - JWT 인증
  - OAuth 연동 가능

## 데이터베이스
- **주 데이터베이스**: PostgreSQL
  - 템플릿, 사용자, 권한, 태그 등 저장
  - JPA/Hibernate ORM 사용
- **검색 엔진**: Elasticsearch (후순위 도입 가능)
  - 키워드/태그 기반 검색 최적화
- **캐시**: Redis
  - 세션 캐시
  - 추천 프롬프트 캐시

## 인프라/배포
- Docker / GitLab CI
- 사내 쿠버네티스 클러스터 or EC2 기반 배포

## 폴더 구조 (Hexagonal / Clean Architecture 기반)
```
/src/main/java/com/gongdel/promptserver
│
├── domain/
│   ├── model/                     # Entity, VO, Enum 등 순수 도메인 모델
│   │   └── PromptTemplate.java
│   │   └── PromptCategory.java
│   │   └── User.java
│   └── service/                  # 도메인 로직 정의 (비즈니스 정책)
│       └── PromptDomainService.java
│
├── application/
│   ├── port/
│   │   ├── in/                   # UseCase 정의 (인터페이스)
│   │   │   ├── command/          # UseCase 커맨드 객체
│   │   │   │   └── RegisterPromptCommand.java
│   │   │   ├── query/            # UseCase 쿼리 객체
│   │   │   │   └── SearchPromptQuery.java
│   │   │   └── RegisterPromptUseCase.java
│   │   └── out/                  # 외부 시스템 의존 인터페이스
│   │       ├── command/          # 아웃바운드 포트 커맨드 객체
│   │       │   └── SavePromptCommand.java
│   │       ├── query/            # 아웃바운드 포트 쿼리 객체
│   │       │   └── FindPromptQuery.java
│   │       └── LoadPromptPort.java
│   └── service/                  # UseCase 구현체
│       └── RegisterPromptService.java
│
├── adapter/
│   ├── in/
│   │   ├── rest/                 # API Controller 등 수신 어댑터
│   │   │   └── PromptController.java
│   │   └── dto/                  # 외부 요청/응답 DTO
│   │       ├── request/          # Request DTO
│   │       │   └── PromptRequest.java
│   │       └── response/         # Response DTO
│   │           └── PromptResponse.java
│   └── out/
│       ├── persistence/         # DB 저장소 어댑터
│       │   ├── entity/          # JPA 엔티티
│       │   │   └── PromptJpaEntity.java
│       │   ├── repository/      # JPA 리포지토리
│       │   │   └── PromptJpaRepository.java
│       │   └── adapter/         # 영속성 어댑터
│       │       └── PromptPersistenceAdapter.java
│       └── client/              # 외부 API 연동 어댑터
│           └── NotionClient.java
│
├── config/                      # 설정 클래스
│   └── WebSecurityConfig.java
│   └── OpenApiConfig.java
│   └── PersistenceConfig.java
│
├── common/                      # 전역 공통 요소
│   ├── exception/               # 예외 처리
│   │   ├── ApplicationException.java
│   │   └── GlobalExceptionHandler.java
│   ├── response/                # 표준 응답 형식
│   │   └── ApiResponse.java
│   └── logging/                 # 로깅 관련
│       └── LoggingAspect.java
│
└── PromptServerApplication.java
```

## 테스트 전략
- **단위 테스트**:
  - DTO 및 도메인 서비스 단위 검증
  - JUnit5 + Mockito

- **통합 테스트**:
  - REST API 레벨 통합 테스트
  - Testcontainers 사용

- **자동화 도구**:
  - GitLab CI 기반 테스트 자동 실행 및 배포 전 체크

## 코드 컨벤션
- **문서화**:
  - 모든 공개 API와 메소드에 JavaDoc 작성
  - 주석은 한글로 작성하여 도메인 용어 이해도 향상

- **명명 규칙**:
  - 클래스: PascalCase (예: PromptTemplate)
  - 메소드/변수: camelCase (예: registerPrompt)
  - 상수: SNAKE_CASE (예: MAX_PROMPT_LENGTH)

- **코드 스타일**:
  - 롬복 활용: `@Data` 대신 세부 어노테이션 사용 (`@Getter`, `@Builder` 등)
  - 커맨드 객체: 유스케이스 파라미터를 커맨드 객체로 캡슐화
