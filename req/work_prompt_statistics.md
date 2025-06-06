# 프롬프트 통계 API 개발 계획

## 1. 도메인 설계

### 1.1 도메인 모델

```java
// PromptStatistics (프롬프트 통계)
- totalCount: Long (총 프롬프트 개수)
- comparisonPeriod: ComparisonPeriod (비교 기간)
- comparisonResult: ComparisonResult (비교 결과)

// ComparisonPeriod (비교 기간)
- type: PeriodType (DAY, WEEK, MONTH)
- startDate: LocalDateTime
- endDate: LocalDateTime

// ComparisonResult (비교 결과)
- currentCount: Long (현재 기간 프롬프트 수)
- previousCount: Long (이전 기간 프롬프트 수)
- percentageChange: Double (변동률)
```

## 2. 계층별 구현 계획

### 2.1 Port (인터페이스)

```java
// LoadPromptStatisticsPort
- loadTotalPromptCount(): Long
- loadPromptCountByPeriod(ComparisonPeriod period): Long
```

### 2.2 UseCase

```java
// PromptStatisticsQueryService
- getPromptStatistics(ComparisonPeriod period): PromptStatisticsResponse
```

### 2.3 Controller

```java
// PromptStatisticsController
- GET /api/v1/dashboard/prompt-statistics
  - Query Parameter: periodType (DAY, WEEK, MONTH)
```

## 3. 구현 단계

1. **도메인 모델 구현**
    - PromptStatistics, ComparisonPeriod, ComparisonResult 클래스 구현
    - PeriodType enum 구현

2. **Port 구현**
    - LoadPromptStatisticsPort 인터페이스 정의
    - JPA 기반 구현체 작성

3. **UseCase 구현**
    - PromptStatisticsQueryService 구현
    - 통계 계산 로직 구현

4. **Controller 구현**
    - REST API 엔드포인트 구현
    - 요청/응답 DTO 구현

5. **테스트 작성**
    - 단위 테스트
    - 통합 테스트

## 4. API 응답 예시

```json
{
  "totalPromptCount": 1500,
  "comparisonResult": {
    "currentCount": 100,
    "previousCount": 80,
    "percentageChange": 25.0
  },
  "periodType": "WEEK"
}
```

## 5. 성능 최적화

### 5.1 데이터베이스 최적화

- 인덱스 설계
  ```sql
  CREATE INDEX idx_prompt_created_at ON prompt (created_at);
  CREATE INDEX idx_prompt_user_id_created_at ON prompt (user_id, created_at);
  CREATE INDEX idx_prompt_statistics ON prompt (created_at, status);
  ```
- 쿼리 최적화
    - N+1 문제 해결
    - 벌크 연산을 위한 네이티브 쿼리 사용

### 5.2 캐싱 전략

- Redis 캐시 구현
    - 캐시 키 설계: `prompt:statistics:{periodType}:{startDate}:{endDate}`
    - TTL 설정
        - 일간 통계: 1시간
        - 주간 통계: 6시간
        - 월간 통계: 24시간

### 5.3 비동기 처리

- 통계 데이터 사전 계산 (스케줄러)
- 이벤트 기반 업데이트

### 5.4 성능 모니터링

- 메트릭 수집
    - API 응답 시간
    - 캐시 히트율
    - 데이터베이스 쿼리 실행 시간
    - 메모리 사용량
- 모니터링 도구
    - Prometheus + Grafana
    - ELK Stack
    - New Relic

### 5.5 페이징 및 제한

- 기본 페이지 크기: 100
- 최대 페이지 크기: 1000

## 6. 성능 테스트 계획

### 6.1 부하 테스트

- 동시 사용자: 100명
- 초당 요청 수: 50회
- 테스트 기간: 1시간

### 6.2 성능 목표

- API 응답 시간: 95% 요청이 200ms 이내
- 캐시 히트율: 80% 이상
- 데이터베이스 CPU 사용률: 70% 이하

## 7. 고려사항

### 7.1 확장성

- 추후 다른 통계 지표 추가 가능성 고려
- 기간 비교 로직의 유연한 확장

### 7.2 에러 처리

- 잘못된 기간 타입 요청 처리
- 데이터 없음 처리

### 7.3 보안

- 인증된 사용자만 접근 가능하도록 설정
- 권한 체크
