# 📝 프롬프트 조회 기능 구현 작업 계획서

## 1. 기능 요구사항

- 최근 프롬프트 조회: 전체 공개(PUBLIC) 및 발행(PUBLISHED)된 최신 프롬프트 목록 제공, 페이징 지원
- 내 프롬프트 개수 조회: 로그인 사용자가 생성한 프롬프트의 상태별(DRAFT, PUBLISHED, ARCHIVED) 개수 집계
- 내 프롬프트 목록 조회: 로그인 사용자가 생성한 프롬프트 목록 제공, 상태별 필터 및 페이징 지원

## 2. API 설계

- 최근 프롬프트 조회: GET /api/v1/prompts/recent
    - Query: page, size
    - Response: PageResponse<PromptListResponse>
- 내 프롬프트 개수 조회: GET /api/v1/prompts/my/count
    - Response: PromptCountResponse (totalCount, draftCount, publishedCount, archivedCount)
- 내 프롬프트 목록 조회: GET /api/v1/prompts/my
    - Query: status, page, size
    - Response: PageResponse<PromptListResponse>

## 3. 도메인 모델 및 UseCase

- PromptSearchCondition: title, description, tag, categoryId, status, sortType, pageable, createdById 등
- PromptStatus: DRAFT, PUBLISHED, ARCHIVED, DELETED
- PromptsQueryUseCase
    - Page<PromptSearchResult> findRecentPrompts(Pageable pageable)
    - PromptCountResult countMyPrompts(Long userId)
    - Page<PromptSearchResult> findMyPrompts(Long userId, PromptStatus status, Pageable pageable)

## 4. Controller 및 DTO

- PromptQueryController에 신규 엔드포인트 추가
- Swagger 문서화, 예외 처리, 응답 포맷 표준화
- PromptCountResponse 등 응답 DTO 설계

## 5. 보안 및 권한

- 인증 사용자만 내 프롬프트 관련 API 접근 가능
- 내 프롬프트/개수 API는 본인 데이터만 반환
- 최근 프롬프트 조회는 공개 데이터만 노출

## 6. 성능 및 테스트

- createdAt, createdById, status 등 인덱스 최적화
- 페이징 처리로 대량 데이터 대응
- 단위/통합 테스트, 인증/인가, 페이징, 상태별 필터 테스트

## 7. 구현 우선순위

1. 최근 프롬프트 조회 API
2. 내 프롬프트 개수 조회 API
3. 내 프롬프트 목록 조회 API

---
이 문서는 프롬프트 조회(최근/내 프롬프트) 기능의 전체적인 작업 흐름과 우선순위를 안내합니다. 실제 개발 시 각 단계별 세부 구현은 별도 설계서 및 API 명세를 참고하세요.

---

## ✅ 현재까지 논의된 개선 방향 및 향후 개선 계획

### 1. 레포지토리 중복 최소화

- 별도의 레포지토리 클래스를 만들지 않고, 기존 PromptTemplateQueryRepository를 확장하여 내 프롬프트 조회 등 다양한 조건을 지원하도록 개선
- 검색 조건(PromptSearchCondition)에 statusFilters, visibilityFilters, searchKeyword, userId, isMyPrompts 등 필드 확장
- buildPredicates 등 내부 쿼리 빌더 메서드에서 조건 분기 처리로 다양한 검색 시나리오 대응

### 2. Controller 계층 분리 및 확장성 확보

- 내 프롬프트 조회 등 특수 케이스는 별도의 엔드포인트로 분리하되, 내부적으로는 공통 검색 로직을 재사용
- UI/UX 요구에 따라 정렬, 필터, 검색 등 다양한 파라미터를 지원하도록 API 설계

### 3. 성능 및 유지보수성

- 인덱스 최적화(사용자, 상태, 공개범위 등)
- 단일 레포지토리 구조로 쿼리 최적화 및 코드 일관성 유지
- 향후 기능 확장(예: 좋아요순, 조회순 정렬, 통합 검색 등) 시에도 기존 구조를 활용하여 중복 최소화

### 4. 향후 개선 계획

- 커스텀 검색 조건이 많아질 경우, QueryDSL Predicate 조합을 위한 유틸리티 분리 고려
- 대용량 데이터 대응을 위한 커서 기반 페이징, 캐싱 전략 도입
- ElasticSearch 등 전문 검색엔진 연동 검토(검색 성능 및 확장성 강화)
- 통계/분석 API 추가(내 프롬프트 활동량, 인기 프롬프트 등)
