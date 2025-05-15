# 🧩 PRD - 프롬프트 템플릿 중앙화 서버

---

## 📌 프로젝트 개요 (Project Overview)

- **프로젝트 목적**: 사내에서 반복적으로 사용하는 프롬프트를 역할/목적/도메인별로 등록·공유·검색·재사용할 수 있는 중앙화된 프롬프트 템플릿 서버 구축
- **주요 기능**: 프롬프트 등록, 검색, 필터링, 버전 관리, 즐겨찾기, 역할 기반 공유 및 조회
- **핵심 사용자 대상**:
  - 백엔드 개발자: API 설계, 아키텍처 설계 프롬프트 등
  - 프론트엔드 개발자: UI 기능 정의, 테스트케이스 자동화
  - 데이터 사이언티스트: 분석 자동화, 전처리 템플릿 활용
  - 디자이너: 기능 정의, UX 메시지 생성 등
- **개발 기간 / 마일스톤**:
  - 1주차: 기능 정의 및 와이어프레임 설계
  - 2~3주차: MVP 기능 구현 (프론트/백엔드)
  - 4주차: 통합 테스트 및 배포

---

## 🧩 핵심 기능 목록 (Core Features)

| 기능 이름                       | 설명                                                       | 우선순위 | 상태   |
|--------------------------------|------------------------------------------------------------|----------|--------|
| 프롬프트 등록                  | 역할/카테고리/예시 포함 프롬프트 템플릿 등록 기능          | 높음     | 미정   |
| 프롬프트 검색                  | 키워드 및 태그 기반 검색 기능                             | 높음     | 미정   |
| 프롬프트 상세 보기             | 본문, 예시, 메타정보 등 포함한 프롬프트 상세 페이지        | 높음     | 미정   |
| 프롬프트 수정 및 삭제          | 등록된 템플릿의 수정 및 삭제 기능                         | 중간     | 미정   |
| 프롬프트 버전 관리             | 템플릿 변경 이력 저장 및 이전 버전 복원 기능              | 중간     | 미정   |
| 마이 프롬프트 / 즐겨찾기       | 내가 만든 템플릿 목록 및 즐겨찾기 목록 조회 기능           | 중간     | 미정   |
| 팀/조직 공유 설정              | 프롬프트 공개 범위 설정 (전체/팀/개인)                    | 높음     | 미정   |
| 권한 기반 접근 제어            | 역할 및 소속 팀에 따라 등록/수정/열람 권한 설정            | 높음     | 미정   |

---

## 🏗️ 시스템 아키텍처 (System Architecture)

자세한 아키텍처 내용은 각각의 기술 스택 문서를 참조하세요:
- 백엔드: `backend.md`
- 프론트엔드: `frontend.md`

---

## 🧪 테스트 전략 (Testing Strategy)

- 🧱 기본 폴더 구조 (Hexagonal / Clean Architecture 기반)
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
│       └── client/              # 외부 API 연동 어댑터 (예: Notion, Slack)
│           └── NotionClient.java
│
├── config/                      # 설정 클래스, 의존성 주입, Security, Swagger 등
│   └── WebSecurityConfig.java
│   └── OpenApiConfig.java
│   └── PersistenceConfig.java
│
├── common/                      # 전역 공통 요소 (에러 처리, Response Wrapper 등)
│   ├── exception/
│   └── response/
│   └── logging/
│
└── PromptServerApplication.java


- **단위 테스트**:  
  - DTO 및 도메인 서비스 단위 검증  
  - JUnit5 + Mockito

- **통합 테스트**:  
  - REST API 레벨 통합 테스트  
  - Testcontainers 사용 예정

- **E2E 테스트**:  
  - Cypress 기반 사용자 시나리오 자동화 테스트

- **자동화 도구**:  
  - GitLab CI 기반 테스트 자동 실행 및 배포 전 체크

---

## 🛠️ 기술 스택 (Tech Stack)

- **언어**: Java 17, TypeScript
- **프레임워크**: Spring Boot, Next.js
- **라이브러리**: Lombok, React, Tailwind, React Query, JPA
- **기타 도구**: Swagger (OpenAPI), GitLab, Docker, Redis (세션 캐시)

---

## 📁 폴더 구조 (File Structure)

