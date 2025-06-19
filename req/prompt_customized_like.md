# 프롬프트 좋아요(Like) 기능 1차 기획서

## 1. 목적 및 개요

- 사용자가 프롬프트 템플릿에 좋아요를 추가/취소할 수 있는 기능 제공
- 좋아요 수 실시간 집계 및 캐싱
- 내가 좋아요한 프롬프트 목록 조회
- CQRS, DDD, 헥사고날 아키텍처 기반 설계

## 2. 데이터 모델

### 2.1 엔티티: PromptLike

| 필드명              | 타입       | 설명      | 제약조건           |
|------------------|----------|---------|----------------|
| id               | BigInt   | PK      | Auto Increment |
| userId           | BigInt   | 사용자 ID  | FK, Not Null   |
| promptTemplateId | BigInt   | 프롬프트 ID | FK, Not Null   |
| createdAt        | DateTime | 생성 일시   | Not Null       |
| updatedAt        | DateTime | 수정 일시   | Not Null       |

- 유니크 제약: (userId, promptTemplateId)
- 인덱스:
    - promptTemplateId (좋아요 수 조회용)
    - (userId, createdAt) (내가 좋아요한 프롬프트 정렬용)

### 2.2 Projection: PromptLikeCount

| 필드명              | 타입       | 설명          | 제약조건       |
|------------------|----------|-------------|------------|
| promptTemplateId | BigInt   | 프롬프트 ID     | PK, FK     |
| likeCount        | Long     | 좋아요 수       | Default: 0 |
| updatedAt        | DateTime | 마지막 업데이트 일시 | Not Null   |

## 3. API 설계

### 3.1 엔드포인트

- POST /api/prompts/{id}/like      : 좋아요 추가 (멱등성 보장)
- DELETE /api/prompts/{id}/like      : 좋아요 취소 (멱등성 보장)
- GET /api/prompts/{id}/like-status : 좋아요 상태/카운트 조회 (명확한 네이밍)
- GET /api/prompts/liked?page=1&size=20 : 내가 좋아요한 프롬프트 목록 (페이징 쿼리 명시)

### 3.2 응답 DTO 예시

```java
public record LikeResponseDto(boolean success, long likeCount) {}
public record LikeStatusDto(boolean isLiked, long likeCount) {}
public record LikedPromptsResponseDto(List<PromptTemplateDto> items, long total, int page, int size) {}
```

## 4. 아키텍처 설계 (CQRS/DDD/헥사고날)

### 4.1 Command 포트 (쓰기)

- AddPromptLikePort: 좋아요 추가 (멱등성 보장)
- RemovePromptLikePort: 좋아요 취소 (멱등성 보장)
- UpdatePromptLikeCountPort: 좋아요 수 캐시 갱신
- PublishLikeEventPort: 좋아요 이벤트 발행 (비동기 확장성 고려)

### 4.2 Query 포트 (읽기)

- LoadPromptLikeStatusPort: 특정 프롬프트에 대한 내 좋아요 여부/카운트 조회
- FindLikedPromptsPort: 내가 좋아요한 프롬프트 목록 조회
- LoadPromptLikeCountPort: 프롬프트별 좋아요 수 조회 (Projection 활용)

### 4.3 어댑터

- CommandAdapter: JPA/Redis 연동, 쓰기 처리
    - Redis INCR/DECR 또는 Lua 스크립트로 동시성/원자성 보장
- QueryAdapter: JPA/Redis 연동, 읽기 최적화
    - @Query JPQL, fetch join 등으로 N+1 문제 방지
    - Projection 활용한 대용량 조회 최적화

### 4.4 유스케이스

- AddPromptLikeUseCase: 좋아요 추가
    - Redis 캐시 무효화 처리
    - 이벤트 발행
- RemovePromptLikeUseCase: 좋아요 취소
    - Redis 캐시 무효화 처리
    - 이벤트 발행
- GetPromptLikeStatusUseCase: 좋아요 상태/카운트 조회
- GetLikedPromptsUseCase: 내가 좋아요한 프롬프트 목록 조회

## 5. 성능 및 확장 고려사항

### 5.1 캐싱 전략

- Redis 캐시 활용(좋아요 수)
    - LRU + TTL 병행 적용
    - 동적 TTL: 템플릿 수정 빈도에 따라 조정
- 캐시 무효화: 좋아요 추가/취소 시 즉시 처리

### 5.2 동시성 제어

- Redis INCR/DECR 명령어 활용
- Lua 스크립트로 원자적 연산 보장
- 낙관적 락(Optimistic Lock) 적용

### 5.3 비동기 처리

- Kafka 등 이벤트 발행
    - 좋아요 추가/취소 이벤트
    - 인기 프롬프트 집계
    - 사용자 피드 업데이트 등 확장성 고려

### 5.4 조회 성능

- Projection Entity 활용
- 복합 인덱스 최적화
- 읽기 전용 트랜잭션 적용

## 6. 테스트 전략

### 6.1 단위 테스트

- 유스케이스/어댑터 단위 테스트
- 동시성 테스트
- 캐시 무효화 테스트

### 6.2 통합 테스트

- API 통합 테스트(인증/예외 포함)
- Redis 연동 테스트
- 이벤트 발행/구독 테스트

### 6.3 성능 테스트

- JMeter/k6를 활용한 부하 테스트
- 동시 요청 처리 성능 측정
- 캐시 히트율 모니터링

## 7. 구현 우선순위

1. Command/Query 포트 및 어댑터 구현
2. 유스케이스 구현
3. 컨트롤러 구현
4. 테스트 작성
5. 성능 테스트 및 최적화

## 8. 예상 소요 시간

- 포트/어댑터: 1일
- 유스케이스: 1일
- 컨트롤러/테스트: 1일
- 성능 테스트/최적화: 1일
- 총 4일 예상

## 9. 구현 세부계획

### 9.1 도메인/엔티티 설계

- PromptLike, PromptLikeCount JPA 엔티티 설계 및 매핑
- 유니크 제약, 인덱스, 생성/수정일시 자동 처리

### 9.2 포트/어댑터 구현

- Command/Query 포트 인터페이스 정의
- JPA/Redis 기반 CommandAdapter, QueryAdapter 구현
- Redis Lua 스크립트/INCR/DECR 활용

### 9.3 유스케이스 구현

- AddPromptLikeUseCase: 좋아요 추가(멱등성, 캐시 무효화, 이벤트 발행)
- RemovePromptLikeUseCase: 좋아요 취소(멱등성, 캐시 무효화, 이벤트 발행)
- GetPromptLikeStatusUseCase: 좋아요 상태/카운트 조회
- GetLikedPromptsUseCase: 내가 좋아요한 프롬프트 목록 조회

### 9.4 API 컨트롤러/DTO

- REST 엔드포인트 구현
- LikeResponseDto, LikeStatusDto, LikedPromptsResponseDto 등 DTO 설계

### 9.5 이벤트/캐시/동시성 처리

- Redis 캐시 적용, 캐시 무효화 로직
- Kafka 등 이벤트 발행 구조 설계
- 동시성 제어(Lua, 낙관적 락 등)

### 9.6 테스트/문서화

- 단위/통합/성능 테스트 코드 작성
- Swagger(OpenAPI) 기반 API 문서화

## 2차 상세 계획: 좋아요 집계 및 목록/상세 조회 반영

### 1. 엔티티 설계 및 매핑 (완료)

- `PromptLikeEntity`: 사용자별 좋아요 기록 (user, promptTemplate 복합 유니크)
- `PromptLikeCountEntity`: 프롬프트별 좋아요 수 집계 (promptTemplateId, likeCount, updatedAt)
- 엔티티 설계 및 JPA 매핑 완료

### 2. 서비스/비즈니스 로직 구현

- 좋아요 추가/취소 시 PromptLikeEntity 생성/삭제 및 PromptLikeCountEntity의 likeCount 증가/감소
- 트랜잭션 처리 및 동시성 제어(낙관적 락, @Version 등) 적용
- 좋아요 수 집계는 PromptLikeCountEntity에서 관리, 필요시 Redis 캐시 적용

### 3. 쿼리/리포지토리 구현

- 프롬프트별 좋아요 수 조회: PromptLikeCountEntity에서 likeCount 조회
- 내가 좋아요 했는지 여부 조회: PromptLikeEntity에서 existsByUserIdAndPromptTemplateId 사용

### 4. API/DTO 확장

- 목록/상세 조회 API 응답 DTO에 `likeCount`, `isLiked` 필드 추가
- 프론트엔드에서 각 프롬프트 카드/상세에 좋아요 수 및 내 좋아요 여부 표시

### 5. 테스트 및 검증

- 단위/통합 테스트: 좋아요 추가/취소, 집계 동기화, 동시성 테스트
- API 통합 테스트: 목록/상세 조회 시 좋아요 수 및 내 좋아요 여부 정상 노출 확인

### 6. 성능/운영 고려

- 대량 트래픽 대응을 위한 Redis 캐시 적용(좋아요 수)
- 집계 테이블과 상세 테이블 분리로 조회 성능 최적화
- 이벤트 발행 구조(확장성 고려, Kafka 등) 설계
