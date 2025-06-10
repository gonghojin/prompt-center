# 프롬프트 즐겨찾기, 조회수, 좋아요 관리 기능 설계 및 작업 계획서

## 1. 목표

- 프롬프트 카드 및 상세 페이지에서 즐겨찾기, 조회수, 좋아요(Like) 기능을 제공한다.
- 대시보드, 프롬프트 탐색, 내 프롬프트 관리 등에서 통계 및 필터링에 활용한다.

## 2. 데이터 모델 설계

### 2.1 즐겨찾기(Favorite)

| 필드               | 타입       | 설명          | 제약조건                        |
|------------------|----------|-------------|-----------------------------|
| id               | BigInt   | 즐겨찾기 고유 식별자 | Primary Key, Auto Increment |
| userId           | BigInt   | 사용자 ID      | Foreign Key, Not Null       |
| promptTemplateId | BigInt   | 템플릿 ID      | Foreign Key, Not Null       |
| createdAt        | DateTime | 생성 일시       | Not Null                    |

> **유니크 제약:** (userId, promptTemplateId)

### 2.2 좋아요(Like)

| 필드               | 타입       | 설명         | 제약조건                        |
|------------------|----------|------------|-----------------------------|
| id               | BigInt   | 좋아요 고유 식별자 | Primary Key, Auto Increment |
| userId           | BigInt   | 사용자 ID     | Foreign Key, Not Null       |
| promptTemplateId | BigInt   | 템플릿 ID     | Foreign Key, Not Null       |
| createdAt        | DateTime | 좋아요 생성 일시  | Not Null                    |

> **유니크 제약:** (userId, promptTemplateId)

### 2.3 조회수 집계(PromptViewCount)

| 필드               | 타입       | 설명          | 제약조건                     |
|------------------|----------|-------------|--------------------------|
| promptTemplateId | BigInt   | 템플릿 ID      | Primary Key, Foreign Key |
| totalViewCount   | Long     | 누적 조회수      | Default: 0               |
| updatedAt        | DateTime | 마지막 업데이트 일시 | Nullable                 |

### 2.4 조회 이력(PromptViewLog)

| 필드               | 타입       | 설명           | 제약조건                                |
|------------------|----------|--------------|-------------------------------------|
| id               | UUID     | 조회 기록 고유 식별자 | Primary Key                         |
| promptTemplateId | BigInt   | 템플릿 ID       | Foreign Key, Not Null               |
| userId           | BigInt   | 사용자 ID       | Foreign Key, Nullable (비로그인 사용자 대응) |
| ipAddress        | String   | 사용자 IP 주소    | Nullable                            |
| viewedAt         | DateTime | 조회 일시        | Not Null                            |

👉 조건: 최근 1시간 내 동일 사용자/아이피가 본 경우 → 카운트 제외

- 로그인 사용자: userId + promptTemplateId 로 체크
- 비로그인 사용자: ipAddress + promptTemplateId 로 체크

## 3. API 엔드포인트 설계

### 3.1 즐겨찾기

- POST /api/prompts/{id}/favorite   : 즐겨찾기 추가
- DELETE /api/prompts/{id}/favorite   : 즐겨찾기 삭제
- GET /api/favorites               : 내 즐겨찾기 목록 조회

### 3.2 조회수

- POST /api/prompts/{id}/view       : 프롬프트 조회 기록
- GET /api/prompts/{id}/views      : 프롬프트별 조회수 통계

### 3.3 좋아요

- POST /api/prompts/{id}/like       : 좋아요 추가
- DELETE /api/prompts/{id}/like       : 좋아요 취소
- GET /api/prompts/{id}/likes      : 좋아요 목록/카운트 조회

## 4. 프론트엔드 연동 포인트

- 프롬프트 카드/상세: 즐겨찾기, 좋아요, 조회수 표시 및 토글
- 대시보드: 전체/카테고리별 통계 카드, 최근 프롬프트, 인기순 정렬 등
- 내 프롬프트 관리: 내 즐겨찾기, 내 좋아요, 내 조회수 통계

## 5. 캐싱 및 성능 전략

- Redis 등 인메모리 캐시로 프롬프트별 좋아요/조회수/즐겨찾기 수 실시간 집계
- 집계 데이터는 일정 주기(예: 5분)마다 DB와 동기화

## 6. 구현 우선순위 (상세)

1. **데이터 모델 및 마이그레이션**
    - 좋아요(Like), 조회수(View) 테이블 신규 설계 및 생성
    - Favorite(즐겨찾기) 테이블 제약조건 및 인덱스 점검
    - 마이그레이션 스크립트 작성 및 테스트

2. **API 구현 (백엔드)**
    - 즐겨찾기 추가/삭제/조회 API 구현
    - 좋아요 추가/취소/조회 API 구현
    - 조회수 기록/통계 API 구현
    - 각 API별 인증/권한 처리 및 예외 처리
    - 단위 테스트 및 통합 테스트 작성

3. **프론트엔드 연동 및 UI/UX**
    - 프롬프트 카드/상세 페이지에 즐겨찾기, 좋아요, 조회수 UI 추가
    - 토글/버튼 인터랙션 및 상태 동기화 구현
    - 대시보드, 내 프롬프트 관리, 탐색 페이지 통계 연동
    - 실시간 반영(optimistic update) 및 사용자 피드백 처리

4. **통계/캐싱 최적화**
    - Redis 등 인메모리 캐시 연동(좋아요/조회수/즐겨찾기 수)
    - 집계 데이터의 주기적 DB 동기화 배치 구현
    - 인기순/조회수순 정렬 등 쿼리 최적화
    - 대시보드/탐색/관리 페이지 통계 성능 개선

5. **QA 및 배포**
    - 전체 기능 통합 테스트 및 시나리오 검증
    - 사용자 피드백 반영 및 버그 수정
    - 운영 환경 배포 및 모니터링 설정

## 7. 향후 확장 고려사항

- 즐겨찾기 폴더, 좋아요/조회수 상세 이력, 알림 연동 등
