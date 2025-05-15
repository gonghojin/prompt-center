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
│   │   │   └── RegisterPromptUseCase.java
│   │   └── out/                  # 외부 시스템 의존 인터페이스
│   │       └── LoadPromptPort.java
│   └── usecase/                  # UseCase 구현체
│       └── RegisterPromptService.java
│
├── adapter/
│   ├── in/
│   │   └── rest/                 # API Controller 등 수신 어댑터
│   │       └── PromptController.java
│   └── out/
│       ├── persistence/         # DB 저장소 어댑터
│       │   └── PromptJpaEntity.java
│       │   └── PromptJpaRepository.java
│       │   └── PromptPersistenceAdapter.java
│       └── client/              # 외부 API 연동 어댑터
│           └── NotionClient.java
│
├── config/                      # 설정 클래스
│   └── WebSecurityConfig.java
│   └── OpenApiConfig.java
│   └── PersistenceConfig.java
│
├── common/                      # 전역 공통 요소
│   ├── exception/
│   └── response/
│   └── logging/
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
