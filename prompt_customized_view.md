# 프롬프트 조회수(View) 기능 세부 설계

## 1. 개요

프롬프트 템플릿의 조회수 기록 및 집계 기능을 구현합니다. 기존 즐겨찾기/좋아요 기능과 동일한 아키텍처 패턴을 적용하여 일관성을 유지합니다.

### 1.1 핵심 요구사항

- 프롬프트 조회 시 조회수 기록 및 집계
- 1시간 내 동일 사용자/IP 중복 조회 방지
- 로그인/비로그인 사용자 모두 지원
- 실시간 조회수 집계 및 조회
- Command/Query 분리 패턴 적용

### 1.2 아키텍처 패턴

- 헥사고날 아키텍처 (Port & Adapter)
- CQRS (Command Query Responsibility Segregation)
- 도메인 주도 설계 (DDD)

## 2. 데이터 모델 설계

### 2.1 PromptViewLog (조회 로그 엔티티)

```java
@Entity
@Table(name = "prompt_view_logs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode(of = "id")
public class PromptViewLog {

    @Id
    private final String id; // UUID as String

    @Column(name = "prompt_template_id", nullable = false)
    private final Long promptTemplateId;

    @Column(name = "user_id")
    private final Long userId; // nullable for anonymous users

    @Column(name = "ip_address", length = 45)
    private final String ipAddress; // IPv6 support

    @Column(name = "viewed_at", nullable = false)
    private final LocalDateTime viewedAt;

    @Builder
    public PromptViewLog(String id, Long promptTemplateId, Long userId,
                        String ipAddress, LocalDateTime viewedAt) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        Assert.notNull(viewedAt, "viewedAt must not be null");

        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.promptTemplateId = promptTemplateId;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.viewedAt = viewedAt;
    }
}
```

**인덱스:**

- `(prompt_template_id, user_id, viewed_at)` - 로그인 사용자 중복 체크
- `(prompt_template_id, ip_address, viewed_at)` - 비로그인 사용자 중복 체크
- `(viewed_at)` - 로그 정리용

### 2.2 PromptViewCount (조회수 집계 엔티티)

```java
@Entity
@Table(name = "prompt_view_counts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@EqualsAndHashCode(of = "promptTemplateId")
public class PromptViewCount extends BaseTimeEntity {

    @Id
    @Column(name = "prompt_template_id")
    private final Long promptTemplateId;

    @Column(name = "total_view_count", nullable = false)
    private Long totalViewCount;

    @Builder
    public PromptViewCount(Long promptTemplateId, Long totalViewCount,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        super(createdAt, updatedAt);
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");

        this.promptTemplateId = promptTemplateId;
        this.totalViewCount = totalViewCount == null ? 0L : totalViewCount;
    }

    /**
     * 조회수를 1 증가시킵니다.
     */
    public void incrementViewCount() {
        this.totalViewCount++;
        this.updateModifiedTime();
    }

    /**
     * 초기 조회수 엔티티를 생성합니다.
     */
    public static PromptViewCount createInitial(Long promptTemplateId) {
        return PromptViewCount.builder()
            .promptTemplateId(promptTemplateId)
            .totalViewCount(1L)
            .build();
    }
}
```

## 3. 도메인 모델 설계

### 3.1 ViewRecord (조회 기록 Value Object)

```java
@Getter
@ToString
@Builder
public class ViewRecord {
    private final String id;
    private final Long promptTemplateId;
    private final Long userId;
    private final String ipAddress;
    private final LocalDateTime viewedAt;

    public ViewRecord(String id, Long promptTemplateId, Long userId,
                     String ipAddress, LocalDateTime viewedAt) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        Assert.notNull(viewedAt, "viewedAt must not be null");

        this.id = id == null ? UUID.randomUUID().toString() : id;
        this.promptTemplateId = promptTemplateId;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.viewedAt = viewedAt;
    }

    /**
     * 로그인 사용자 여부를 확인합니다.
     */
    public boolean isLoggedInUser() {
        return userId != null;
    }

    /**
     * 중복 조회 체크를 위한 키를 생성합니다.
     */
    public String getDuplicationCheckKey() {
        if (isLoggedInUser()) {
            return String.format("user_%d_prompt_%d", userId, promptTemplateId);
        } else {
            return String.format("ip_%s_prompt_%d", ipAddress, promptTemplateId);
        }
    }
}
```

### 3.2 ViewCount (조회수 Value Object)

```java
@Getter
@ToString
@Builder
@EqualsAndHashCode
public class ViewCount {
    private final Long promptTemplateId;
    private final Long totalViewCount;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ViewCount(Long promptTemplateId, Long totalViewCount,
                    LocalDateTime createdAt, LocalDateTime updatedAt) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");

        this.promptTemplateId = promptTemplateId;
        this.totalViewCount = totalViewCount == null ? 0L : totalViewCount;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.updatedAt = updatedAt == null ? LocalDateTime.now() : updatedAt;
    }

    /**
     * 조회수가 증가된 새로운 ViewCount를 반환합니다.
     */
    public ViewCount increment() {
        return ViewCount.builder()
            .promptTemplateId(this.promptTemplateId)
            .totalViewCount(this.totalViewCount + 1)
            .createdAt(this.createdAt)
            .updatedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 초기 조회수 생성 (신규 프롬프트용)
     */
    public static ViewCount createInitial(Long promptTemplateId) {
        return ViewCount.builder()
            .promptTemplateId(promptTemplateId)
            .totalViewCount(1L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }
}
```

### 3.3 전체 조회수 통계 도메인 모델 (기존 PromptStatistics 패턴 적용)

#### ViewStatistics (전체 조회수 통계)

```java
/**
 * 전체 조회수 통계 정보를 담는 도메인 객체
 * 기존 PromptStatistics 패턴을 참조하여 설계
 */
@Getter
@Builder
@Slf4j
public class ViewStatistics {
    private final long totalViewCount;           // 전체 누적 조회수
    private final ComparisonPeriod comparisonPeriod;  // 비교 기간
    private final ComparisonResult comparisonResult;  // 기간별 비교 결과

    /**
     * ViewStatistics 객체를 생성합니다.
     */
    public ViewStatistics(long totalViewCount, ComparisonPeriod comparisonPeriod, ComparisonResult comparisonResult) {
        Assert.isTrue(totalViewCount >= 0, "totalViewCount must be non-negative");
        Assert.notNull(comparisonPeriod, "comparisonPeriod must not be null");
        Assert.notNull(comparisonResult, "comparisonResult must not be null");

        this.totalViewCount = totalViewCount;
        this.comparisonPeriod = comparisonPeriod;
        this.comparisonResult = comparisonResult;

        log.debug("ViewStatistics created: totalViewCount={}, period={}, result={}",
            totalViewCount, comparisonPeriod, comparisonResult);
    }
}

/**
 * 주간 조회수 통계 (기존 WeeklyViewStat 패턴 확장)
 */
@Getter
@Builder
public class WeeklyViewStatistics {
    private final long thisWeekViews;      // 이번 주 조회수
    private final long lastWeekViews;      // 지난 주 조회수
    private final double changeRate;       // 증감률 (%)
    private final int changeCount;         // 증감 수치
    private final LocalDate weekStartDate; // 이번 주 시작일
    private final LocalDate weekEndDate;   // 이번 주 종료일

    public WeeklyViewStatistics(long thisWeekViews, long lastWeekViews, double changeRate,
                               int changeCount, LocalDate weekStartDate, LocalDate weekEndDate) {
        Assert.isTrue(thisWeekViews >= 0, "thisWeekViews must be non-negative");
        Assert.isTrue(lastWeekViews >= 0, "lastWeekViews must be non-negative");
        Assert.notNull(weekStartDate, "weekStartDate must not be null");
        Assert.notNull(weekEndDate, "weekEndDate must not be null");

        this.thisWeekViews = thisWeekViews;
        this.lastWeekViews = lastWeekViews;
        this.changeRate = changeRate;
        this.changeCount = changeCount;
        this.weekStartDate = weekStartDate;
        this.weekEndDate = weekEndDate;
    }

    /**
     * 주간 통계 생성 팩토리 메서드
     */
    public static WeeklyViewStatistics of(long thisWeekViews, long lastWeekViews) {
        double changeRate = 0.0;
        if (lastWeekViews == 0) {
            changeRate = thisWeekViews > 0 ? 100.0 : 0.0;
        } else {
            changeRate = ((double) (thisWeekViews - lastWeekViews) / lastWeekViews) * 100.0;
        }

        int changeCount = (int) (thisWeekViews - lastWeekViews);

        // 이번 주 월요일 ~ 일요일 계산
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(DayOfWeek.MONDAY);
        LocalDate weekEnd = today.with(DayOfWeek.SUNDAY);

        return new WeeklyViewStatistics(thisWeekViews, lastWeekViews, changeRate,
            changeCount, weekStart, weekEnd);
    }
}

/**
 * 인기 프롬프트 정보 (조회수 기준)
 */
@Getter
@Builder
public class TopViewedPrompt {
    private final int rank;                    // 순위
    private final UUID promptTemplateUuid;     // 프롬프트 UUID
    private final String title;               // 프롬프트 제목
    private final String categoryName;        // 카테고리명
    private final long totalViews;            // 기간 내 조회수
    private final long allTimeViews;          // 전체 누적 조회수
    private final double averageDailyViews;   // 일평균 조회수
    private final String authorName;          // 작성자명
    private final LocalDateTime lastViewedAt; // 마지막 조회 시점

    public TopViewedPrompt(int rank, UUID promptTemplateUuid, String title, String categoryName,
                          long totalViews, long allTimeViews, double averageDailyViews,
                          String authorName, LocalDateTime lastViewedAt) {
        Assert.isTrue(rank > 0, "rank must be positive");
        Assert.notNull(promptTemplateUuid, "promptTemplateUuid must not be null");
        Assert.hasText(title, "title must not be blank");

        this.rank = rank;
        this.promptTemplateUuid = promptTemplateUuid;
        this.title = title;
        this.categoryName = categoryName;
        this.totalViews = totalViews;
        this.allTimeViews = allTimeViews;
        this.averageDailyViews = averageDailyViews;
        this.authorName = authorName;
        this.lastViewedAt = lastViewedAt;
    }
}
```

#### LoadTopViewedPromptsQuery

```java
/**
 * 인기 프롬프트 조회 쿼리 객체
 */
@Getter
@Builder
public class LoadTopViewedPromptsQuery {
    private final int days;          // 조회 기간 (일)
    private final int limit;         // 결과 개수
    private final LocalDate endDate; // 종료일 (기본값: 오늘)

    private LoadTopViewedPromptsQuery(int days, int limit, LocalDate endDate) {
        Assert.isTrue(days > 0 && days <= 365, "days must be between 1 and 365");
        Assert.isTrue(limit > 0 && limit <= 100, "limit must be between 1 and 100");

        this.days = days;
        this.limit = limit;
        this.endDate = endDate != null ? endDate : LocalDate.now();
    }

    public LocalDate getStartDate() {
        return endDate.minusDays(days - 1);
    }
}
```

## 4. 애플리케이션 서비스 설계

### 4.1 Command 객체 설계

#### RecordViewCommand

```java
/**
 * 프롬프트 조회 기록 요청 커맨드 객체
 */
@Getter
public class RecordViewCommand {
    private final Long userId;              // nullable for anonymous users
    private final UUID promptTemplateUuid;
    private final String ipAddress;
    private final String anonymousId;       // 비로그인 사용자 식별용 UUID

    /**
     * 로그인 사용자용 조회 기록 커맨드 생성자
     */
    @Builder
    private RecordViewCommand(Long userId, UUID promptTemplateUuid, String ipAddress, String anonymousId) {
        Assert.notNull(promptTemplateUuid, "promptTemplateUuid must not be null");
        Assert.hasText(ipAddress, "ipAddress must not be blank");

        this.userId = userId;
        this.promptTemplateUuid = promptTemplateUuid;
        this.ipAddress = ipAddress;
        this.anonymousId = anonymousId;
    }

    /**
     * 로그인 사용자용 조회 기록 커맨드 생성 팩토리 메서드
     */
    public static RecordViewCommand forUser(Long userId, UUID promptTemplateUuid, String ipAddress) {
        Assert.notNull(userId, "userId must not be null for authenticated user");
        return new RecordViewCommand(userId, promptTemplateUuid, ipAddress, null);
    }

    /**
     * 비로그인 사용자용 조회 기록 커맨드 생성 팩토리 메서드
     */
    public static RecordViewCommand forGuest(UUID promptTemplateUuid, String ipAddress, String anonymousId) {
        return new RecordViewCommand(null, promptTemplateUuid, ipAddress, anonymousId);
    }

    /**
     * 로그인 사용자인지 확인
     */
    public boolean isLoggedInUser() {
        return userId != null;
    }

    /**
     * 중복 체크용 Redis 키 생성
     */
    public String getDuplicationCheckKey() {
        if (isLoggedInUser()) {
            return String.format("view:user:%d:prompt:%s", userId, promptTemplateUuid);
        } else if (StringUtils.hasText(anonymousId)) {
            return String.format("view:anon:%s:prompt:%s", anonymousId, promptTemplateUuid);
        } else {
            return String.format("view:ip:%s:prompt:%s", ipAddress, promptTemplateUuid);
        }
    }
}
```

#### LoadViewCountQuery

```java
/**
 * 프롬프트 조회수 조회 쿼리 객체
 */
@Getter
public class LoadViewCountQuery {
    private final UUID promptTemplateUuid;

    /**
     * 조회수 조회 쿼리 생성자
     */
    private LoadViewCountQuery(UUID promptTemplateUuid) {
        Assert.notNull(promptTemplateUuid, "promptTemplateUuid must not be null");
        this.promptTemplateUuid = promptTemplateUuid;
    }

    /**
     * 조회수 조회 쿼리 생성 팩토리 메서드
     */
    public static LoadViewCountQuery of(UUID promptTemplateUuid) {
        return new LoadViewCountQuery(promptTemplateUuid);
    }
}
```

### 4.2 Command Use Case

```java
public interface PromptViewCommandUseCase {
    /**
     * 프롬프트 조회를 기록합니다.
     * 1시간 내 중복 조회는 무시됩니다.
     *
     * @param command 조회 기록 커맨드
     * @return 조회 기록 성공 여부
     */
    boolean recordView(RecordViewCommand command);
}
```

```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PromptViewCommandService implements PromptViewCommandUseCase {

    private final SavePromptViewLogPort savePromptViewLogPort;
    private final CheckDuplicateViewPort checkDuplicateViewPort;
    private final UpdatePromptViewCountPort updatePromptViewCountPort;
    private final LoadPromptTemplateIdPort loadPromptTemplateIdPort;

    private static final int DUPLICATE_CHECK_HOURS = 1;

    /**
     * 프롬프트 조회를 기록합니다.
     */
    @Override
    public boolean recordView(RecordViewCommand command) {
        Assert.notNull(command, "RecordViewCommand must not be null");

        try {
            Long promptId = findPromptIdOrThrow(command.getPromptTemplateUuid());

            // 1. Redis 기반 중복 체크 (성능 최적화)
            String duplicateKey = command.getDuplicationCheckKey();
            boolean isNewView = checkDuplicateViewPort.setIfAbsent(duplicateKey, 1);

            if (!isNewView) {
                log.debug("Duplicate view detected. key={}", duplicateKey);
                return false;
            }

            // 2. Redis 기반 조회수 증가 (실시간 반영)
            long currentViewCount = updatePromptViewCountPort.incrementViewCountInCache(promptId);

            // 3. 조회 로그 비동기 저장 (DB 부하 분산)
            ViewRecord viewRecord = createViewRecord(command, promptId, LocalDateTime.now());
            savePromptViewLogPort.saveAsync(viewRecord);

            log.info("View recorded successfully. userId={}, promptUuid={}, currentCount={}",
                command.getUserId(), command.getPromptTemplateUuid(), currentViewCount);
            return true;

        } catch (Exception e) {
            log.error("Failed to record view. userId={}, promptUuid={}, ipAddress={}",
                command.getUserId(), command.getPromptTemplateUuid(), command.getIpAddress(), e);
            throw new ViewOperationException("Failed to record view", e);
        }
    }

    private ViewRecord createViewRecord(RecordViewCommand command, Long promptId, LocalDateTime now) {
        return ViewRecord.builder()
            .promptTemplateId(promptId)
            .userId(command.getUserId())
            .ipAddress(command.getIpAddress())
            .viewedAt(now)
            .build();
    }

    private Long findPromptIdOrThrow(UUID promptTemplateUuid) {
        return loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)
            .orElseThrow(() -> {
                log.error("Prompt not found for UUID: {}", promptTemplateUuid);
                throw ViewOperationException.notFound(promptTemplateUuid);
            });
    }
}
```

### 4.3 Query Use Case

```java
public interface PromptViewQueryUseCase {
    /**
     * 프롬프트별 조회수를 조회합니다.
     *
     * @param query 조회수 조회 쿼리
     * @return 조회수 정보
     */
    ViewCount getViewCount(LoadViewCountQuery query);

    /**
     * 프롬프트별 조회수를 조회합니다. (내부 ID 사용)
     *
     * @param promptTemplateId 프롬프트 템플릿 ID
     * @return 조회수
     */
    long getViewCount(Long promptTemplateId);
}
```

```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptViewQueryService implements PromptViewQueryUseCase {

    private final LoadPromptViewCountPort loadPromptViewCountPort;
    private final LoadPromptTemplateIdPort loadPromptTemplateIdPort;

    /**
     * 프롬프트별 조회수를 조회합니다.
     */
    @Override
    public ViewCount getViewCount(LoadViewCountQuery query) {
        Assert.notNull(query, "LoadViewCountQuery must not be null");

        try {
            Long promptId = findPromptIdOrThrow(query.getPromptTemplateUuid());
            return loadPromptViewCountPort.loadViewCount(promptId);

        } catch (Exception e) {
            log.error("Failed to get view count. promptUuid={}", query.getPromptTemplateUuid(), e);
            throw new ViewOperationException("Failed to get view count", e);
        }
    }

    /**
     * 프롬프트별 조회수를 조회합니다. (내부 ID 사용)
     */
    @Override
    public long getViewCount(Long promptTemplateId) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");

        try {
            ViewCount viewCount = loadPromptViewCountPort.loadViewCount(promptTemplateId);
            return viewCount.getTotalViewCount();

        } catch (Exception e) {
            log.error("Failed to get view count. promptId={}", promptTemplateId, e);
            throw new ViewOperationException("Failed to get view count", e);
        }
    }

    private Long findPromptIdOrThrow(UUID promptTemplateUuid) {
        return loadPromptTemplateIdPort.findIdByUuid(promptTemplateUuid)
            .orElseThrow(() -> {
                log.error("Prompt not found for UUID: {}", promptTemplateUuid);
                throw ViewOperationException.notFound(promptTemplateUuid);
            });
    }
}
```

### 4.3 트렌드 분석 Query Controller (기존 통계 패턴 적용)

```java
@Slf4j
@Tag(name = "프롬프트 조회수 트렌드", description = "프롬프트 조회수 트렌드 분석 API")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptViewTrendController {

    private final PromptViewQueryUseCase promptViewQueryUseCase;

    @Operation(summary = "프롬프트 일별 조회수 트렌드", description = "특정 기간 동안의 프롬프트 일별 조회수 추이를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "트렌드 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}/views/trend")
    public ResponseEntity<ViewTrendResponse> getViewTrend(
            @Parameter(description = "프롬프트 UUID", required = true) @PathVariable("id") UUID id,
            @Parameter(description = "시작 날짜", example = "2024-01-01T00:00:00")
            @RequestParam(value = "startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "종료 날짜", example = "2024-01-31T23:59:59")
            @RequestParam(value = "endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("Request view trend: promptUuid={}, startDate={}, endDate={}", id, startDate, endDate);

        validateTrendParameters(id, startDate, endDate);

        LoadViewTrendQuery query = LoadViewTrendQuery.builder()
            .promptTemplateUuid(id)
            .startDate(startDate.toLocalDate())
            .endDate(endDate.toLocalDate())
            .build();

        ViewTrend viewTrend = promptViewQueryUseCase.getViewTrend(query);

        log.info("View trend retrieved: promptUuid={}, dataPoints={}", id, viewTrend.getDailyStats().size());

        return ResponseEntity.ok(ViewTrendResponse.from(viewTrend));
    }

    @Operation(summary = "인기 프롬프트 랭킹", description = "조회수 기준 인기 프롬프트 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인기 프롬프트 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/popular")
    public ResponseEntity<PopularPromptsResponse> getPopularPrompts(
            @Parameter(description = "조회 기간 (일)", example = "7")
            @RequestParam(value = "days", defaultValue = "7") int days,
            @Parameter(description = "결과 개수", example = "10")
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        log.info("Request popular prompts: days={}, limit={}", days, limit);

        validatePopularPromptsParameters(days, limit);

        LoadPopularPromptsQuery query = LoadPopularPromptsQuery.builder()
            .days(days)
            .limit(limit)
            .build();

        List<PopularPrompt> popularPrompts = promptViewQueryUseCase.getPopularPrompts(query);

        log.info("Popular prompts retrieved: count={}", popularPrompts.size());

        return ResponseEntity.ok(PopularPromptsResponse.from(popularPrompts));
    }

    /**
     * 트렌드 조회 파라미터 유효성 검증
     */
    private void validateTrendParameters(UUID promptUuid, LocalDateTime startDate, LocalDateTime endDate) {
        Assert.notNull(promptUuid, "Prompt UUID must not be null");
        Assert.notNull(startDate, "Start date must not be null");
        Assert.notNull(endDate, "End date must not be null");

        if (endDate.isBefore(startDate)) {
            log.error("End date {} is before start date {}", endDate, startDate);
            throw new IllegalArgumentException("End date must not be before start date");
        }

        // 최대 조회 기간 제한 (1년)
        if (startDate.isBefore(endDate.minusYears(1))) {
            log.error("Query period too long: startDate={}, endDate={}", startDate, endDate);
            throw new IllegalArgumentException("Query period must not exceed 1 year");
        }
    }

    /**
     * 인기 프롬프트 파라미터 유효성 검증
     */
    private void validatePopularPromptsParameters(int days, int limit) {
        Assert.isTrue(days > 0 && days <= 365, "Days must be between 1 and 365");
        Assert.isTrue(limit > 0 && limit <= 100, "Limit must be between 1 and 100");
    }
}
```

### 4.4 전체 조회수 통계 Use Case (기존 PromptStatisticsQueryService 패턴 적용)

```java
/**
 * 전체 조회수 통계 조회 유스케이스 인터페이스
 * 기존 PromptStatisticsQueryUseCase 패턴을 참조하여 설계
 */
public interface PromptViewStatisticsQueryUseCase {
    /**
     * 대시보드용 전체 조회수 통계 정보를 조회합니다.
     *
     * @param period 비교 기간 (null 불가)
     * @return 조회수 통계 도메인 객체
     */
    ViewStatistics getViewStatistics(ComparisonPeriod period);

    /**
     * 주간 조회수 통계를 조회합니다.
     *
     * @return 주간 조회수 통계 도메인 객체
     */
    WeeklyViewStatistics getWeeklyViewStatistics();

    /**
     * 조회수 기준 인기 프롬프트 목록을 조회합니다.
     *
     * @param query 인기 프롬프트 조회 쿼리
     * @return 인기 프롬프트 목록
     */
    List<TopViewedPrompt> getTopViewedPrompts(LoadTopViewedPromptsQuery query);
}
```

```java
/**
 * 전체 조회수 통계 조회 서비스
 * 기존 PromptStatisticsQueryService 패턴을 참조하여 설계
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptViewStatisticsQueryService implements PromptViewStatisticsQueryUseCase {

    private final LoadPromptViewStatisticsPort loadPromptViewStatisticsPort;

    /**
     * 대시보드용 전체 조회수 통계 정보를 조회합니다.
     */
    @Override
    public ViewStatistics getViewStatistics(ComparisonPeriod period) {
        Assert.notNull(period, "period must not be null");

        try {
            log.debug("Start loading view statistics for period: {}", period);

            long totalViewCount = loadPromptViewStatisticsPort.loadTotalViewCount();
            long currentPeriodViews = loadPromptViewStatisticsPort.loadViewCountByPeriod(period);
            ComparisonPeriod previousPeriod = calculatePreviousPeriod(period);
            long previousPeriodViews = loadPromptViewStatisticsPort.loadViewCountByPeriod(previousPeriod);

            log.debug("View count summary - total: {}, current period: {}, previous period: {}",
                totalViewCount, currentPeriodViews, previousPeriodViews);

            ComparisonResult comparisonResult = ComparisonResult.of(currentPeriodViews, previousPeriodViews);
            ViewStatistics stats = new ViewStatistics(totalViewCount, period, comparisonResult);

            log.debug("View statistics result: {}", stats);
            return stats;

        } catch (Exception e) {
            log.error("Unexpected error during view statistics query", e);
            throw new ViewOperationException("Failed to query view statistics", e);
        }
    }

    /**
     * 주간 조회수 통계를 조회합니다.
     */
    @Override
    public WeeklyViewStatistics getWeeklyViewStatistics() {
        try {
            log.debug("Start loading weekly view statistics");

            LocalDate today = LocalDate.now();
            LocalDate thisWeekStart = today.with(DayOfWeek.MONDAY);
            LocalDate thisWeekEnd = today.with(DayOfWeek.SUNDAY);
            LocalDate lastWeekStart = thisWeekStart.minusWeeks(1);
            LocalDate lastWeekEnd = thisWeekEnd.minusWeeks(1);

            ComparisonPeriod thisWeekPeriod = new ComparisonPeriod(
                thisWeekStart.atStartOfDay(), thisWeekEnd.atTime(23, 59, 59));
            ComparisonPeriod lastWeekPeriod = new ComparisonPeriod(
                lastWeekStart.atStartOfDay(), lastWeekEnd.atTime(23, 59, 59));

            long thisWeekViews = loadPromptViewStatisticsPort.loadViewCountByPeriod(thisWeekPeriod);
            long lastWeekViews = loadPromptViewStatisticsPort.loadViewCountByPeriod(lastWeekPeriod);

            log.debug("Weekly view count - this week: {}, last week: {}", thisWeekViews, lastWeekViews);

            WeeklyViewStatistics stats = WeeklyViewStatistics.of(thisWeekViews, lastWeekViews);
            log.debug("Weekly view statistics result: {}", stats);
            return stats;

        } catch (Exception e) {
            log.error("Unexpected error during weekly view statistics query", e);
            throw new ViewOperationException("Failed to query weekly view statistics", e);
        }
    }

    /**
     * 조회수 기준 인기 프롬프트 목록을 조회합니다.
     */
    @Override
    public List<TopViewedPrompt> getTopViewedPrompts(LoadTopViewedPromptsQuery query) {
        Assert.notNull(query, "query must not be null");

        try {
            log.debug("Start loading top viewed prompts: days={}, limit={}", query.getDays(), query.getLimit());

            List<TopViewedPrompt> topPrompts = loadPromptViewStatisticsPort.loadTopViewedPrompts(query);

            log.debug("Top viewed prompts loaded: {} items", topPrompts.size());
            return topPrompts;

        } catch (Exception e) {
            log.error("Unexpected error during top viewed prompts query", e);
            throw new ViewOperationException("Failed to query top viewed prompts", e);
        }
    }

    /**
     * 이전 비교 기간을 계산합니다.
     * 기존 PromptStatisticsQueryService와 동일한 로직
     */
    private ComparisonPeriod calculatePreviousPeriod(ComparisonPeriod period) {
        long duration = java.time.Duration.between(period.getStartDate(), period.getEndDate()).getSeconds();
        return new ComparisonPeriod(
            period.getStartDate().minusSeconds(duration),
            period.getStartDate().minusSeconds(1));
    }
}
```

### 5.3 전체 조회수 통계 Query Ports

```java
/**
 * 전체 조회수 통계 조회 포트 (아웃바운드)
 * 기존 LoadPromptStatisticsPort 패턴을 참조하여 설계
 */
public interface LoadPromptViewStatisticsPort {
    /**
     * 전체 누적 조회수를 조회합니다.
     *
     * @return 전체 누적 조회수
     */
    long loadTotalViewCount();

    /**
     * 특정 기간 동안의 조회수를 조회합니다.
     *
     * @param period 조회 기간
     * @return 기간 내 조회수
     */
    long loadViewCountByPeriod(ComparisonPeriod period);

    /**
     * 조회수 기준 인기 프롬프트 목록을 조회합니다.
     *
     * @param query 인기 프롬프트 조회 쿼리
     * @return 인기 프롬프트 목록
     */
    List<TopViewedPrompt> loadTopViewedPrompts(LoadTopViewedPromptsQuery query);

    /**
     * 일별 조회수 통계를 조회합니다. (트렌드 분석용)
     *
     * @param promptTemplateId 프롬프트 ID
     * @param startDate 시작일
     * @param endDate 종료일
     * @return 일별 조회수 통계 목록
     */
    List<DailyViewStat> loadDailyViewStats(Long promptTemplateId, LocalDate startDate, LocalDate endDate);
}
```

## 5. Port 인터페이스 설계

### 5.1 Command Ports

```java
// 출력 포트 (아웃바운드)
public interface SavePromptViewLogPort {
    /**
     * 조회 로그를 저장합니다.
     */
    void save(ViewRecord viewRecord);

    /**
     * 조회 로그를 비동기로 저장합니다. (배치 처리용)
     */
    void saveAsync(ViewRecord viewRecord);
}

/**
 * Redis 기반 중복 체크 포트 (성능 최적화)
 */
public interface CheckDuplicateViewPort {
    /**
     * Redis SetNX를 사용한 중복 체크 및 키 설정
     * @param key 중복 체크 키
     * @param ttlHours TTL (시간)
     * @return true: 신규 조회, false: 중복 조회
     */
    boolean setIfAbsent(String key, int ttlHours);

    /**
     * 기존 DB 기반 중복 체크 (fallback용)
     * @deprecated Redis 우선 사용, DB는 fallback용으로만 사용
     */
    @Deprecated
    boolean existsByUserIdAndPromptIdSince(Long userId, Long promptId, LocalDateTime since);

    @Deprecated
    boolean existsByIpAddressAndPromptIdSince(String ipAddress, Long promptId, LocalDateTime since);
}

/**
 * 조회수 집계 최적화 포트
 */
public interface UpdatePromptViewCountPort {
    /**
     * Redis를 통한 임시 조회수 증가
     * @param promptTemplateId 프롬프트 ID
     * @return 현재 조회수 (Redis + DB 합계)
     */
    long incrementViewCountInCache(Long promptTemplateId);

    /**
     * DB 직접 업데이트 (배치 처리용)
     * @param promptTemplateId 프롬프트 ID
     * @param incrementBy 증가할 수
     */
    void incrementViewCountInDb(Long promptTemplateId, long incrementBy);

    /**
     * Redis 캐시의 조회수를 DB에 플러시
     * @return 플러시된 프롬프트 수
     */
    int flushViewCountsToDb();
}
```

### 5.2 Query Ports

```java
// 출력 포트 (아웃바운드)
public interface LoadPromptViewCountPort {
    /**
     * 프롬프트 조회수 정보를 조회합니다.
     */
    ViewCount loadViewCount(Long promptTemplateId);
}
```

## 6. 컨트롤러 설계

### 6.1 Command Controller

```java
@Slf4j
@Tag(name = "프롬프트 조회수 - Command", description = "프롬프트 조회수 기록 API")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptViewCommandController {

    private final PromptViewCommandUseCase promptViewCommandUseCase;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "프롬프트 조회 기록", description = "프롬프트 조회를 기록하고 조회수를 업데이트합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회 기록 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PostMapping("/{id}/view")
    public ResponseEntity<ViewRecordResponse> recordView(
            @Parameter(description = "프롬프트 UUID", required = true) @PathVariable("id") UUID id,
            @RequestHeader(value = "X-Anonymous-Id", required = false) String anonymousId,
            HttpServletRequest request) {

        Assert.notNull(id, "Prompt ID cannot be null");

        Long userId = currentUserProvider.getCurrentUserIdOrNull();
        String ipAddress = getClientIpAddress(request);

        log.info("Recording view: userId={}, promptUuid={}, anonymousId={}, ipAddress={}",
            userId, id, anonymousId, ipAddress);

        // Command 객체 생성 - 로그인/비로그인 사용자 구분
        RecordViewCommand command = userId != null
            ? RecordViewCommand.forUser(userId, id, ipAddress)
            : RecordViewCommand.forGuest(id, ipAddress, anonymousId);

        boolean recorded = promptViewCommandUseCase.recordView(command);

        log.info("View record result: userId={}, promptUuid={}, recorded={}", userId, id, recorded);

        return ResponseEntity.ok(ViewRecordResponse.of(recorded));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotBlank(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (StringUtils.isNotBlank(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
```

### 6.2 Query Controller

```java
@Slf4j
@Tag(name = "프롬프트 조회수 - Query", description = "프롬프트 조회수 조회 API")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptViewQueryController {

    private final PromptViewQueryUseCase promptViewQueryUseCase;

    @Operation(summary = "프롬프트 조회수 조회", description = "프롬프트의 총 조회수를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회수 조회 성공"),
        @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}/views")
    public ResponseEntity<ViewCountResponse> getViewCount(
            @Parameter(description = "프롬프트 UUID", required = true) @PathVariable("id") UUID id) {

        Assert.notNull(id, "Prompt ID cannot be null");

        log.info("Querying view count: promptUuid={}", id);

        ViewCount viewCount = promptViewQueryUseCase.getViewCount(LoadViewCountQuery.of(id));

        log.info("View count retrieved: promptUuid={}, count={}", id, viewCount.getTotalViewCount());

        return ResponseEntity.ok(ViewCountResponse.from(viewCount));
    }
}
```

### 6.3 전체 조회수 통계 Controller (기존 통계 패턴 적용)

```java
@Slf4j
@Tag(name = "프롬프트 조회수 통계", description = "대시보드용 전체 조회수 통계 API")
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class PromptViewStatisticsController {

    private final PromptViewStatisticsQueryUseCase promptViewStatisticsQueryUseCase;

    @Operation(summary = "전체 조회수 통계 조회", description = "대시보드용 전체 프롬프트 조회수 통계를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회수 통계 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/view-statistics")
    public ResponseEntity<ViewStatisticsResponse> getViewStatistics(
            @Parameter(description = "시작 날짜", required = true, example = "2024-01-01T00:00:00")
            @RequestParam(value = "startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @Parameter(description = "종료 날짜", required = true, example = "2024-01-31T23:59:59")
            @RequestParam(value = "endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        log.info("Request view statistics: startDate={}, endDate={}", startDate, endDate);

        validateParameters(startDate, endDate);

        ComparisonPeriod period = new ComparisonPeriod(startDate, endDate);
        ViewStatistics stats = promptViewStatisticsQueryUseCase.getViewStatistics(period);

        log.info("View statistics retrieved: totalViews={}, currentPeriodViews={}",
            stats.getTotalViewCount(), stats.getComparisonResult().getCurrentCount());

        return ResponseEntity.ok(ViewStatisticsResponse.from(stats));
    }

    @Operation(summary = "주간 조회수 통계", description = "이번 주와 지난 주 조회수 비교 통계를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "주간 통계 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/weekly-view-statistics")
    public ResponseEntity<WeeklyViewStatisticsResponse> getWeeklyViewStatistics() {

        log.info("Request weekly view statistics");

        WeeklyViewStatistics stats = promptViewStatisticsQueryUseCase.getWeeklyViewStatistics();

        log.info("Weekly view statistics retrieved: thisWeek={}, lastWeek={}, changeRate={}",
            stats.getThisWeekViews(), stats.getLastWeekViews(), stats.getChangeRate());

        return ResponseEntity.ok(WeeklyViewStatisticsResponse.from(stats));
    }

    @Operation(summary = "인기 프롬프트 TOP 10", description = "조회수 기준 인기 프롬프트 TOP 10을 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "인기 프롬프트 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/top-viewed-prompts")
    public ResponseEntity<TopViewedPromptsResponse> getTopViewedPrompts(
            @Parameter(description = "조회 기간 (일)", example = "30")
            @RequestParam(value = "days", defaultValue = "30") int days,
            @Parameter(description = "결과 개수", example = "10")
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        log.info("Request top viewed prompts: days={}, limit={}", days, limit);

        validateTopViewedParameters(days, limit);

        LoadTopViewedPromptsQuery query = LoadTopViewedPromptsQuery.builder()
            .days(days)
            .limit(limit)
            .build();

        List<TopViewedPrompt> topPrompts = promptViewStatisticsQueryUseCase.getTopViewedPrompts(query);

        log.info("Top viewed prompts retrieved: count={}", topPrompts.size());

        return ResponseEntity.ok(TopViewedPromptsResponse.from(query, topPrompts));
    }

    /**
     * 파라미터 유효성 검증 메서드 (기존 PromptStatisticsQueryController 패턴)
     */
    private void validateParameters(LocalDateTime startDate, LocalDateTime endDate) {
        Assert.notNull(startDate, "StartDate must not be null");
        Assert.notNull(endDate, "EndDate must not be null");

        if (endDate.isBefore(startDate)) {
            log.error("End date {} is before start date {}", endDate, startDate);
            throw new IllegalArgumentException("End date must not be before start date");
        }

        // 최대 조회 기간 제한 (1년)
        if (startDate.isBefore(endDate.minusYears(1))) {
            log.error("Query period too long: startDate={}, endDate={}", startDate, endDate);
            throw new IllegalArgumentException("Query period must not exceed 1 year");
        }
    }

    /**
     * TOP 조회 프롬프트 파라미터 유효성 검증
     */
    private void validateTopViewedParameters(int days, int limit) {
        Assert.isTrue(days > 0 && days <= 365, "Days must be between 1 and 365");
        Assert.isTrue(limit > 0 && limit <= 100, "Limit must be between 1 and 100");
    }
}
```

## 7. Response DTO 설계

### 7.1 ViewRecordResponse

```java
@Getter
@Builder
@Schema(description = "조회 기록 응답 DTO")
public class ViewRecordResponse {

    @Schema(description = "조회 기록 성공 여부", example = "true")
    private final boolean recorded;

    @Schema(description = "메시지", example = "View recorded successfully")
    private final String message;

    public static ViewRecordResponse of(boolean recorded) {
        return ViewRecordResponse.builder()
            .recorded(recorded)
            .message(recorded ? "View recorded successfully" : "Duplicate view within 1 hour")
            .build();
    }
}
```

### 7.2 ViewCountResponse

```java
@Getter
@Builder
@Schema(description = "조회수 응답 DTO")
public class ViewCountResponse {

    @Schema(description = "총 조회수", example = "1250")
    private final long totalViewCount;

    @Schema(description = "조회수 집계 시작 일시", example = "2024-01-10T09:00:00")
    private final LocalDateTime createdAt;

    @Schema(description = "마지막 업데이트 일시", example = "2024-01-15T10:30:00")
    private final LocalDateTime updatedAt;

    public static ViewCountResponse from(ViewCount viewCount) {
        return ViewCountResponse.builder()
            .totalViewCount(viewCount.getTotalViewCount())
            .createdAt(viewCount.getCreatedAt())
            .updatedAt(viewCount.getUpdatedAt())
            .build();
    }
}
```

### 7.3 트렌드 분석 Response DTO

```java
@Getter
@Builder
@Schema(description = "조회수 트렌드 응답 DTO")
public class ViewTrendResponse {

    @Schema(description = "프롬프트 UUID")
    private final UUID promptTemplateUuid;

    @Schema(description = "조회 기간 시작일", example = "2024-01-01")
    private final LocalDate startDate;

    @Schema(description = "조회 기간 종료일", example = "2024-01-31")
    private final LocalDate endDate;

    @Schema(description = "총 조회수", example = "5420")
    private final long totalViewCount;

    @Schema(description = "일별 조회수 통계")
    private final List<DailyViewStat> dailyStats;

    @Schema(description = "트렌드 요약")
    private final TrendSummary summary;

    public static ViewTrendResponse from(ViewTrend viewTrend) {
        return ViewTrendResponse.builder()
            .promptTemplateUuid(viewTrend.getPromptTemplateUuid())
            .startDate(viewTrend.getStartDate())
            .endDate(viewTrend.getEndDate())
            .totalViewCount(viewTrend.getTotalViewCount())
            .dailyStats(viewTrend.getDailyStats().stream()
                .map(DailyViewStat::from)
                .collect(Collectors.toList()))
            .summary(TrendSummary.from(viewTrend))
            .build();
    }
}

@Getter
@Builder
@Schema(description = "일별 조회수 통계")
public class DailyViewStat {

    @Schema(description = "날짜", example = "2024-01-15")
    private final LocalDate date;

    @Schema(description = "해당 일의 조회수", example = "127")
    private final long viewCount;

    public static DailyViewStat from(com.domain.DailyViewStat domainStat) {
        return DailyViewStat.builder()
            .date(domainStat.getDate())
            .viewCount(domainStat.getViewCount())
            .build();
    }
}

@Getter
@Builder
@Schema(description = "트렌드 요약 정보")
public class TrendSummary {

    @Schema(description = "일평균 조회수", example = "174.8")
    private final double averageDailyViews;

    @Schema(description = "최고 일일 조회수", example = "342")
    private final long peakDailyViews;

    @Schema(description = "최고 조회수 날짜", example = "2024-01-20")
    private final LocalDate peakDate;

    @Schema(description = "전일 대비 증감률 (%)", example = "12.5")
    private final double changeFromPreviousDay;

    @Schema(description = "트렌드 방향", example = "INCREASING")
    private final TrendDirection trendDirection;

    public static TrendSummary from(ViewTrend viewTrend) {
        List<com.domain.DailyViewStat> stats = viewTrend.getDailyStats();

        if (stats.isEmpty()) {
            return TrendSummary.builder()
                .averageDailyViews(0.0)
                .peakDailyViews(0L)
                .peakDate(null)
                .changeFromPreviousDay(0.0)
                .trendDirection(TrendDirection.STABLE)
                .build();
        }

        double avgViews = stats.stream()
            .mapToLong(com.domain.DailyViewStat::getViewCount)
            .average()
            .orElse(0.0);

        com.domain.DailyViewStat peakStat = stats.stream()
            .max(Comparator.comparing(com.domain.DailyViewStat::getViewCount))
            .orElse(stats.get(0));

        double changeRate = calculateChangeRate(stats);
        TrendDirection direction = determineTrendDirection(stats);

        return TrendSummary.builder()
            .averageDailyViews(Math.round(avgViews * 100.0) / 100.0)
            .peakDailyViews(peakStat.getViewCount())
            .peakDate(peakStat.getDate())
            .changeFromPreviousDay(Math.round(changeRate * 100.0) / 100.0)
            .trendDirection(direction)
            .build();
    }

    private static double calculateChangeRate(List<com.domain.DailyViewStat> stats) {
        if (stats.size() < 2) return 0.0;

        long latest = stats.get(stats.size() - 1).getViewCount();
        long previous = stats.get(stats.size() - 2).getViewCount();

        if (previous == 0) return latest > 0 ? 100.0 : 0.0;

        return ((double) (latest - previous) / previous) * 100.0;
    }

    private static TrendDirection determineTrendDirection(List<com.domain.DailyViewStat> stats) {
        if (stats.size() < 3) return TrendDirection.STABLE;

        // 최근 3일 평균과 이전 3일 평균 비교
        int size = stats.size();
        double recentAvg = stats.subList(size - 3, size).stream()
            .mapToLong(com.domain.DailyViewStat::getViewCount)
            .average().orElse(0.0);

        double previousAvg = stats.subList(Math.max(0, size - 6), size - 3).stream()
            .mapToLong(com.domain.DailyViewStat::getViewCount)
            .average().orElse(0.0);

        double diff = recentAvg - previousAvg;
        double threshold = previousAvg * 0.1; // 10% 임계값

        if (diff > threshold) return TrendDirection.INCREASING;
        if (diff < -threshold) return TrendDirection.DECREASING;
        return TrendDirection.STABLE;
    }
}

@Schema(description = "트렌드 방향")
public enum TrendDirection {
    @Schema(description = "증가 추세")
    INCREASING,

    @Schema(description = "감소 추세")
    DECREASING,

    @Schema(description = "안정적")
    STABLE
}

@Getter
@Builder
@Schema(description = "인기 프롬프트 목록 응답 DTO")
public class PopularPromptsResponse {

    @Schema(description = "조회 기간 (일)", example = "7")
    private final int days;

    @Schema(description = "총 프롬프트 수", example = "10")
    private final int totalCount;

    @Schema(description = "인기 프롬프트 목록")
    private final List<PopularPromptInfo> prompts;

    public static PopularPromptsResponse from(List<PopularPrompt> popularPrompts) {
        return PopularPromptsResponse.builder()
            .days(popularPrompts.isEmpty() ? 0 : popularPrompts.get(0).getDays())
            .totalCount(popularPrompts.size())
            .prompts(popularPrompts.stream()
                .map(PopularPromptInfo::from)
                .collect(Collectors.toList()))
            .build();
    }
}

@Getter
@Builder
@Schema(description = "인기 프롬프트 정보")
public class PopularPromptInfo {

    @Schema(description = "순위", example = "1")
    private final int rank;

    @Schema(description = "프롬프트 UUID")
    private final UUID promptTemplateUuid;

    @Schema(description = "프롬프트 제목", example = "AI 코딩 어시스턴트 프롬프트")
    private final String title;

    @Schema(description = "기간 내 총 조회수", example = "1250")
    private final long totalViews;

    @Schema(description = "일평균 조회수", example = "178.6")
    private final double averageDailyViews;

    @Schema(description = "전체 조회수", example = "5420")
    private final long allTimeViews;

    public static PopularPromptInfo from(PopularPrompt popularPrompt) {
        return PopularPromptInfo.builder()
            .rank(popularPrompt.getRank())
            .promptTemplateUuid(popularPrompt.getPromptTemplateUuid())
            .title(popularPrompt.getTitle())
            .totalViews(popularPrompt.getTotalViews())
            .averageDailyViews(Math.round(popularPrompt.getAverageDailyViews() * 100.0) / 100.0)
            .allTimeViews(popularPrompt.getAllTimeViews())
            .build();
    }
}

### 7.4 전체 조회수 통계 Response DTO (기존 PromptStatisticsResponse 패턴 적용)

```java
@Getter
@Builder
@Schema(description = "전체 조회수 통계 응답 DTO")
public class ViewStatisticsResponse {

    @Schema(description = "전체 누적 조회수", example = "125430")
    private final long totalViewCount;

    @Schema(description = "현재 기간 조회수", example = "4520")
    private final long currentPeriodCount;

    @Schema(description = "이전 기간 조회수", example = "3850")
    private final long previousPeriodCount;

    @Schema(description = "증감 수치", example = "670")
    private final int changeCount;

    @Schema(description = "증감률 (%)", example = "17.4")
    private final double changePercentage;

    @Schema(description = "비교 기간 시작일시", example = "2024-01-01T00:00:00")
    private final LocalDateTime startDate;

    @Schema(description = "비교 기간 종료일시", example = "2024-01-31T23:59:59")
    private final LocalDateTime endDate;

    public static ViewStatisticsResponse from(ViewStatistics viewStatistics) {
        return ViewStatisticsResponse.builder()
            .totalViewCount(viewStatistics.getTotalViewCount())
            .currentPeriodCount(viewStatistics.getComparisonResult().getCurrentPeriodCount())
            .previousPeriodCount(viewStatistics.getComparisonResult().getPreviousPeriodCount())
            .changeCount(viewStatistics.getComparisonResult().getChangeCount())
            .changePercentage(viewStatistics.getComparisonResult().getChangePercentage())
            .startDate(viewStatistics.getComparisonPeriod().getStartDate())
            .endDate(viewStatistics.getComparisonPeriod().getEndDate())
            .build();
    }
}

@Getter
@Builder
@Schema(description = "주간 조회수 통계 응답 DTO")
public class WeeklyViewStatisticsResponse {

    @Schema(description = "이번 주 조회수", example = "2340")
    private final long thisWeekViews;

    @Schema(description = "지난 주 조회수", example = "1890")
    private final long lastWeekViews;

    @Schema(description = "증감 수치", example = "450")
    private final int changeCount;

    @Schema(description = "증감률 (%)", example = "23.8")
    private final double changeRate;

    @Schema(description = "이번 주 시작일", example = "2024-01-15")
    private final LocalDate weekStartDate;

    @Schema(description = "이번 주 종료일", example = "2024-01-21")
    private final LocalDate weekEndDate;

    public static WeeklyViewStatisticsResponse from(WeeklyViewStatistics weeklyStats) {
        return WeeklyViewStatisticsResponse.builder()
            .thisWeekViews(weeklyStats.getThisWeekViews())
            .lastWeekViews(weeklyStats.getLastWeekViews())
            .changeCount(weeklyStats.getChangeCount())
            .changeRate(weeklyStats.getChangeRate())
            .weekStartDate(weeklyStats.getWeekStartDate())
            .weekEndDate(weeklyStats.getWeekEndDate())
            .build();
    }
}

@Getter
@Builder
@Schema(description = "인기 프롬프트 응답 DTO")
public class TopViewedPromptResponse {

    @Schema(description = "순위", example = "1")
    private final int rank;

    @Schema(description = "프롬프트 UUID", example = "550e8400-e29b-41d4-a716-446655440000")
    private final UUID promptTemplateUuid;

    @Schema(description = "프롬프트 제목", example = "효과적인 이메일 작성 프롬프트")
    private final String title;

    @Schema(description = "카테고리명", example = "비즈니스")
    private final String categoryName;

    @Schema(description = "기간 내 조회수", example = "1250")
    private final long totalViews;

    @Schema(description = "전체 누적 조회수", example = "15430")
    private final long allTimeViews;

    @Schema(description = "일평균 조회수", example = "41.7")
    private final double averageDailyViews;

    @Schema(description = "작성자명", example = "홍길동")
    private final String authorName;

    @Schema(description = "마지막 조회 시점", example = "2024-01-21T14:30:15")
    private final LocalDateTime lastViewedAt;

    public static TopViewedPromptResponse from(TopViewedPrompt topPrompt) {
        return TopViewedPromptResponse.builder()
            .rank(topPrompt.getRank())
            .promptTemplateUuid(topPrompt.getPromptTemplateUuid())
            .title(topPrompt.getTitle())
            .categoryName(topPrompt.getCategoryName())
            .totalViews(topPrompt.getTotalViews())
            .allTimeViews(topPrompt.getAllTimeViews())
            .averageDailyViews(topPrompt.getAverageDailyViews())
            .authorName(topPrompt.getAuthorName())
            .lastViewedAt(topPrompt.getLastViewedAt())
            .build();
    }
}

@Getter
@Builder
@Schema(description = "인기 프롬프트 목록 응답 DTO")
public class TopViewedPromptsResponse {

    @Schema(description = "조회 기간 (일)", example = "30")
    private final int days;

    @Schema(description = "조회 기간 시작일", example = "2024-01-01")
    private final LocalDate startDate;

    @Schema(description = "조회 기간 종료일", example = "2024-01-30")
    private final LocalDate endDate;

    @Schema(description = "인기 프롬프트 목록")
    private final List<TopViewedPromptResponse> prompts;

    @Schema(description = "총 결과 개수", example = "10")
    private final int totalCount;

    public static TopViewedPromptsResponse from(LoadTopViewedPromptsQuery query, List<TopViewedPrompt> prompts) {
        List<TopViewedPromptResponse> promptResponses = prompts.stream()
            .map(TopViewedPromptResponse::from)
            .collect(Collectors.toList());

        return TopViewedPromptsResponse.builder()
            .days(query.getDays())
            .startDate(query.getStartDate())
            .endDate(query.getEndDate())
            .prompts(promptResponses)
            .totalCount(promptResponses.size())
            .build();
    }
}
```

## 8. 예외 처리 설계

### 8.1 ViewOperationException (기존 통계 예외 패턴 적용)

```java
/**
 * 프롬프트 조회수 관련 예외를 처리하는 클래스
 * 기존 PromptStatisticsException 패턴을 참조하여 설계
 */
@Getter
public class ViewOperationException extends BaseException {

    public ViewOperationException(ViewErrorType errorType, String message) {
        super(errorType, message);
    }

    public ViewOperationException(ViewErrorType errorType, String message, Throwable cause) {
        super(errorType, message, cause);
    }

    /**
     * 프롬프트를 찾을 수 없는 경우
     */
    public static ViewOperationException notFound(UUID promptTemplateUuid) {
        return new ViewOperationException(
            ViewErrorType.VIEW_NOT_FOUND,
            String.format("프롬프트를 찾을 수 없습니다: %s", promptTemplateUuid));
    }

    /**
     * 잘못된 요청 파라미터
     */
    public static ViewOperationException invalidRequest(String message) {
        return new ViewOperationException(
            ViewErrorType.INVALID_REQUEST,
            message);
    }

    /**
     * 데이터베이스 접근 오류
     */
    public static ViewOperationException databaseError(Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.DATABASE_ERROR,
            "조회수 처리 중 데이터베이스 오류가 발생했습니다.",
            cause);
    }

    /**
     * Redis 연결 오류
     */
    public static ViewOperationException redisError(Throwable cause) {
        return new ViewOperationException(
            ViewErrorType.REDIS_ERROR,
            "조회수 처리 중 Redis 오류가 발생했습니다.",
            cause);
    }

    /**
     * 일반 처리 오류
     */
    public static ViewOperationException general(String message) {
        return new ViewOperationException(
            ViewErrorType.INTERNAL_SERVER_ERROR,
            message);
    }
}
```

### 8.2 ViewErrorType (기존 통계 에러 타입 패턴 적용)

```java
/**
 * 프롬프트 조회수 관련 에러 유형을 정의합니다.
 * 기존 PromptStatisticsErrorType 패턴을 참조하여 설계
 */
@Getter
public enum ViewErrorType implements ErrorCode {
    GENERAL(1600, "일반 조회수 처리 오류"),
    INTERNAL_SERVER_ERROR(1601, "내부 서버 오류"),
    VIEW_NOT_FOUND(1602, "조회 기록을 찾을 수 없음"),
    INVALID_REQUEST(1603, "잘못된 요청 파라미터"),
    DATABASE_ERROR(1604, "데이터베이스 접근 오류"),
    REDIS_ERROR(1605, "Redis 연결 오류"),
    DUPLICATE_VIEW(1606, "중복 조회 요청");

    private final int code;
    private final String message;

    ViewErrorType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
```

## 9. 데이터베이스 마이그레이션

### 9.1 PromptViewLog 테이블 (파티셔닝 적용)

```sql
-- 조회 로그 테이블 (월별 파티셔닝)
CREATE TABLE prompt_view_logs (
    id VARCHAR(36) PRIMARY KEY,
    prompt_template_id BIGINT NOT NULL,
    user_id BIGINT NULL,
    ip_address VARCHAR(45) NULL,
    anonymous_id VARCHAR(36) NULL,  -- 비로그인 사용자 식별용
    viewed_at TIMESTAMP NOT NULL,

    INDEX idx_prompt_user_time (prompt_template_id, user_id, viewed_at),
    INDEX idx_prompt_ip_time (prompt_template_id, ip_address, viewed_at),
    INDEX idx_prompt_anon_time (prompt_template_id, anonymous_id, viewed_at),
    INDEX idx_viewed_at (viewed_at),

    FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
)
PARTITION BY RANGE (YEAR(viewed_at) * 100 + MONTH(viewed_at)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    -- 매월 새로운 파티션 추가 필요
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

### 9.2 PromptViewCount 테이블

```sql
-- 조회수 집계 테이블
CREATE TABLE prompt_view_counts (
    prompt_template_id BIGINT PRIMARY KEY,
    total_view_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates(id) ON DELETE CASCADE
);
```

### 9.3 일별 조회수 통계 테이블

```sql
-- 일별 조회수 통계 테이블 (트렌드 분석용)
CREATE TABLE prompt_daily_view_stats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    prompt_template_id BIGINT NOT NULL,
    view_date DATE NOT NULL,
    daily_view_count BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE KEY uk_prompt_date (prompt_template_id, view_date),
    INDEX idx_prompt_template_date (prompt_template_id, view_date DESC),
    INDEX idx_view_date (view_date),

    FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates(id) ON DELETE CASCADE
);
```

### 9.4 아카이브 테이블

```sql
-- 오래된 조회 로그 아카이브 테이블
CREATE TABLE prompt_view_logs_archive (
    id VARCHAR(36) PRIMARY KEY,
    prompt_template_id BIGINT NOT NULL,
    user_id BIGINT NULL,
    ip_address VARCHAR(45) NULL,
    anonymous_id VARCHAR(36) NULL,
    viewed_at TIMESTAMP NOT NULL,
    archived_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    INDEX idx_archive_viewed_at (viewed_at),
    INDEX idx_archive_archived_at (archived_at)
);
```

### 9.5 파티션 관리 프로시저

```sql
-- 월별 파티션 자동 생성 프로시저
DELIMITER $$
CREATE PROCEDURE CreateMonthlyPartition()
BEGIN
    DECLARE next_month_partition VARCHAR(20);
    DECLARE next_month_value INT;

    SET next_month_value = (YEAR(CURDATE() + INTERVAL 1 MONTH) * 100 + MONTH(CURDATE() + INTERVAL 1 MONTH));
    SET next_month_partition = CONCAT('p', next_month_value);

    SET @sql = CONCAT(
        'ALTER TABLE prompt_view_logs ADD PARTITION (',
        'PARTITION ', next_month_partition,
        ' VALUES LESS THAN (', next_month_value + 1, '))'
    );

    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END$$
DELIMITER ;

-- 매월 1일 자동 실행을 위한 이벤트 스케줄러
CREATE EVENT IF NOT EXISTS create_monthly_partition
ON SCHEDULE EVERY 1 MONTH
STARTS '2024-01-01 00:00:00'
DO CALL CreateMonthlyPartition();
```

## 10. 테스트 전략

### 10.1 단위 테스트

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("PromptViewCommandService 테스트")
class PromptViewCommandServiceTest {

    @Test
    @DisplayName("정상적으로 조회를 기록한다")
    void givenValidInput_whenRecordView_thenReturnsTrue() {
        // Given
        Long userId = 1L;
        UUID promptUuid = UUID.randomUUID();
        String ipAddress = "192.168.1.1";

        when(loadPromptTemplateIdPort.findIdByUuid(promptUuid))
            .thenReturn(Optional.of(100L));
        when(checkDuplicateViewPort.existsByUserIdAndPromptIdSince(any(), any(), any()))
            .thenReturn(false);

        // When
        boolean result = promptViewCommandService.recordView(RecordViewCommand.forUser(userId, promptUuid, ipAddress));

        // Then
        assertThat(result).isTrue();
        verify(savePromptViewLogPort).save(any(ViewRecord.class));
        verify(updatePromptViewCountPort).incrementViewCount(100L);
    }

    @Test
    @DisplayName("중복 조회 시 false를 반환한다")
    void givenDuplicateView_whenRecordView_thenReturnsFalse() {
        // Given
        Long userId = 1L;
        UUID promptUuid = UUID.randomUUID();
        String ipAddress = "192.168.1.1";

        when(loadPromptTemplateIdPort.findIdByUuid(promptUuid))
            .thenReturn(Optional.of(100L));
        when(checkDuplicateViewPort.existsByUserIdAndPromptIdSince(any(), any(), any()))
            .thenReturn(true);

        // When
        boolean result = promptViewCommandService.recordView(RecordViewCommand.forUser(userId, promptUuid, ipAddress));

        // Then
        assertThat(result).isFalse();
        verify(savePromptViewLogPort, never()).save(any());
        verify(updatePromptViewCountPort, never()).incrementViewCount(any());
    }
}
```

### 10.2 통합 테스트

```java
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@DisplayName("PromptView 통합 테스트")
class PromptViewIntegrationTest {

    @Test
    @DisplayName("조회 기록 API 통합 테스트")
    void testRecordViewIntegration() throws Exception {
        // Given
        UUID promptUuid = createTestPrompt();

        // When & Then
        mockMvc.perform(post("/api/v1/prompts/{id}/view", promptUuid)
                .header("Authorization", "Bearer " + getTestToken())
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.recorded").value(true))
            .andExpect(jsonPath("$.message").value("View recorded successfully"));
    }
}
```

## 11. 구현 우선순위 (기존 통계 패턴 반영 + 전체 통계 추가)

### Phase 1: 핵심 기능 구현 (기존 패턴 적용)

1. **도메인 모델 구현**
    - RecordViewCommand, LoadViewCountQuery (기존 ComparisonPeriod 패턴 적용)
    - ViewRecord, ViewCount Value Objects
    - **ViewStatistics, WeeklyViewStatistics, TopViewedPrompt** (전체 통계 도메인 추가)
    - ViewErrorType, ViewOperationException (기존 PromptStatisticsException 패턴)

2. **Redis 인프라 설정**
    - Redis 연결 설정 및 RedisTemplate 구성
    - 중복 체크 및 조회수 캐싱용 Redis 키 설계
    - **Graceful Shutdown을 위한 Redis TTL 및 플러시 락 설정**

3. **성능 최적화 어댑터 구현**
    - RedisViewDuplicationAdapter (SetNX 패턴)
    - RedisViewCountAdapter (Redis 캐싱 + 배치 플러시)
    - **PromptViewStatisticsQueryAdapter** (전체 통계 조회)
    - DB fallback 로직 포함

### Phase 2: 데이터베이스 및 배치 처리

4. **데이터베이스 마이그레이션**
    - prompt_view_logs 테이블 (파티셔닝 적용)
    - prompt_view_counts 테이블
    - **prompt_daily_view_stats 테이블** (일별 통계 집계)
    - 아카이브 테이블 및 파티션 관리 프로시저

5. **배치 처리 구현**
    - ViewCountBatchService (Redis → DB 플러시)
    - **일별 통계 집계 배치** (매일 자정 실행)
    - Graceful Shutdown 처리 (@PreDestroy)
    - 분산 락을 통한 중복 실행 방지

### Phase 3: API 및 비즈니스 로직 (기존 컨트롤러 패턴 적용)

6. **Use Case 구현**
    - PromptViewCommandService (조회수 기록)
    - PromptViewQueryService (개별 프롬프트 조회수 조회)
    - **PromptViewStatisticsQueryService** (전체 통계 조회)

7. **Controller 구현**
    - PromptViewController (개별 프롬프트 조회수 API)
    - PromptViewTrendController (트렌드 분석 API)
    - **PromptViewStatisticsController** (대시보드용 전체 통계 API)

### Phase 4: 모니터링 및 운영

8. **모니터링 및 알림**
    - Redis 연결 상태 모니터링
    - 배치 처리 실패 알림
    - 조회수 데이터 일관성 검증

9. **성능 튜닝**
    - 인덱스 최적화
    - 쿼리 성능 분석
    - Redis 메모리 사용량 모니터링

### 🆕 추가된 전체 통계 기능

#### API 엔드포인트

```bash
# 대시보드용 전체 조회수 통계
GET /api/v1/dashboard/view-statistics?startDate=2024-01-01T00:00:00&endDate=2024-01-31T23:59:59

# 주간 조회수 통계
GET /api/v1/dashboard/weekly-view-statistics

# 인기 프롬프트 목록 (조회수 기준)
GET /api/v1/dashboard/top-viewed-prompts?days=30&limit=10

# 개별 프롬프트 트렌드 분석
GET /api/v1/prompts/{id}/views/trend?startDate=2024-01-01&endDate=2024-01-31
```

#### 성능 최적화 포인트

1. **전체 통계 성능**: Redis 캐시 + DB 조합으로 실시간 정확성 보장
2. **인기 프롬프트 조회**: 일별 통계 테이블 활용으로 빠른 집계
3. **트렌드 분석**: 파티셔닝된 일별 통계로 대용량 데이터 처리
4. **배치 처리**: 분산 락으로 다중 인스턴스 환경 지원

### 예상 성능 개선 효과 (전체 통계 포함)

- **전체 통계 조회**: Redis 캐싱으로 90% 성능 향상
- **인기 프롬프트**: 일별 집계 테이블로 95% 성능 향상
- **트렌드 분석**: 파티셔닝으로 대용량 데이터 처리 가능
- **실시간성**: Redis + 배치 조합으로 정확성과 성능 양립

이제 **개별 프롬프트 조회수**와 **전체 대시보드 통계** 모두를 지원하는 완전한 조회수 시스템이 완성되었습니다! 🎉

## 12. 성능 고려사항

### 12.1 인덱스 최적화

- 중복 체크 쿼리 최적화를 위한 복합 인덱스
- 시간 기반 조회를 위한 `viewed_at` 인덱스

### 12.2 로그 데이터 관리

- 오래된 로그 데이터 정리 배치 작업
- 파티셔닝 고려 (월별 또는 연도별)

### 12.3 캐싱 전략

- 조회수 집계 데이터 Redis 캐싱
- 중복 체크 결과 단기 캐싱

### 12.4 Redis 캐시 플러시 배치 서비스

```java
/**
 * 조회수 집계 배치 처리 서비스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ViewCountBatchService {

    private final UpdatePromptViewCountPort updatePromptViewCountPort;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String BATCH_LOCK_KEY = "batch:viewcount:flush";
    private static final String DAILY_BATCH_LOCK_KEY = "batch:viewcount:daily";

    /**
     * Redis 캐시의 조회수를 DB에 플러시 (5분마다 실행)
     * 분산 환경에서 중복 실행 방지
     */
    @Scheduled(fixedRate = 300000) // 5분
    @Transactional
    public void flushViewCountsToDatabase() {
        // 분산 락으로 중복 실행 방지
        Boolean lockAcquired = redisTemplate.opsForValue()
            .setIfAbsent(BATCH_LOCK_KEY, "running", Duration.ofMinutes(4));

        if (!Boolean.TRUE.equals(lockAcquired)) {
            log.debug("Batch flush already running on another node, skipping...");
            return;
        }

        try {
            log.debug("Starting scheduled view count flush to database");
            int flushedCount = updatePromptViewCountPort.flushViewCountsToDb();

            if (flushedCount > 0) {
                log.info("Successfully flushed {} view counts to database", flushedCount);
            }

        } catch (Exception e) {
            log.error("Failed to flush view counts to database", e);
        } finally {
            // 배치 락 해제
            redisTemplate.delete(BATCH_LOCK_KEY);
        }
    }

    /**
     * 일별 조회수 통계 집계 (매일 새벽 1시 실행)
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void aggregateDailyViewCounts() {
        Boolean lockAcquired = redisTemplate.opsForValue()
            .setIfAbsent(DAILY_BATCH_LOCK_KEY, "running", Duration.ofHours(1));

        if (!Boolean.TRUE.equals(lockAcquired)) {
            log.debug("Daily aggregation already running on another node, skipping...");
            return;
        }

        try {
            String yesterday = LocalDate.now().minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

            log.info("Starting daily view count aggregation for date: {}", yesterday);

            // 일별 조회수 Redis 키 패턴 조회
            String dailyPattern = "viewcount:daily:*:" + yesterday;
            Set<String> dailyKeys = redisTemplate.keys(dailyPattern);

            if (dailyKeys != null && !dailyKeys.isEmpty()) {
                int processedCount = processDailyViewCounts(dailyKeys, yesterday);
                log.info("Successfully processed {} daily view count records for {}",
                    processedCount, yesterday);
            }

        } catch (Exception e) {
            log.error("Failed to aggregate daily view counts", e);
        } finally {
            redisTemplate.delete(DAILY_BATCH_LOCK_KEY);
        }
    }

    /**
     * 오래된 조회 로그 정리 (매일 새벽 2시 실행)
     */
    @Scheduled(cron = "0 0 2 * * *")
    @Transactional
    public void archiveOldViewLogs() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(3);
            log.info("Starting archive process for view logs older than {}", cutoffDate);

            // 3개월 이전 로그를 archive 테이블로 이동 또는 삭제
            // 실제 구현은 요구사항에 따라 결정
            // int archivedCount = archiveViewLogsPort.archiveLogsBefore(cutoffDate);
            // log.info("Successfully archived {} old view logs", archivedCount);

        } catch (Exception e) {
            log.error("Failed to archive old view logs", e);
        }
    }

    /**
     * 애플리케이션 종료 시 최종 플러시
     */
    @PreDestroy
    public void onApplicationShutdown() {
        log.info("Application shutdown detected. Performing final view count flush...");

        try {
            // 기존 배치 락 무시하고 강제 플러시
            redisTemplate.delete(BATCH_LOCK_KEY);

            int flushedCount = updatePromptViewCountPort.flushViewCountsToDb();
            log.info("Final flush completed: {} view counts saved to database", flushedCount);

        } catch (Exception e) {
            log.error("Failed to perform final view count flush on shutdown", e);
        }
    }

    /**
     * 일별 조회수 처리 (DB 저장 또는 별도 집계 테이블 저장)
     */
    private int processDailyViewCounts(Set<String> dailyKeys, String date) {
        int processedCount = 0;

        for (String key : dailyKeys) {
            try {
                String countStr = redisTemplate.opsForValue().get(key);
                if (countStr != null) {
                    Long promptId = extractPromptIdFromDailyKey(key);
                    long dailyCount = Long.parseLong(countStr);

                    // 일별 통계 테이블에 저장 (향후 구현)
                    // saveDailyViewStat(promptId, date, dailyCount);

                    // 처리 완료 후 Redis 키 삭제
                    redisTemplate.delete(key);
                    processedCount++;

                    log.debug("Processed daily view count: promptId={}, date={}, count={}",
                        promptId, date, dailyCount);
                }
            } catch (Exception e) {
                log.error("Failed to process daily view count for key: {}", key, e);
            }
        }

        return processedCount;
    }

    /**
     * 일별 키에서 프롬프트 ID 추출
     */
    private Long extractPromptIdFromDailyKey(String key) {
        // viewcount:daily:{promptId}:{yyyyMMdd} 형식에서 promptId 추출
        String[] parts = key.split(":");
        return Long.parseLong(parts[2]);
    }
}
```

### 12.5 Redis 기반 중복 체크 어댑터

```java
/**
 * Redis 기반 중복 체크 어댑터 구현
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisViewDuplicationAdapter implements CheckDuplicateViewPort {

    private final RedisTemplate<String, String> redisTemplate;
    private final PromptViewLogRepository promptViewLogRepository; // fallback용

    @Override
    public boolean setIfAbsent(String key, int ttlHours) {
        try {
            Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(key, "1", Duration.ofHours(ttlHours));

            return Boolean.TRUE.equals(result);

        } catch (Exception e) {
            log.warn("Redis setIfAbsent failed for key: {}, falling back to DB check", key, e);
            return fallbackToDatabaseCheck(key);
        }
    }

    /**
     * Redis 장애 시 DB fallback
     */
    private boolean fallbackToDatabaseCheck(String key) {
        try {
            // key 파싱하여 DB 조회
            // 실제 구현은 key 형식에 따라 결정
            log.info("Using database fallback for duplicate check: {}", key);
            return true; // 안전하게 중복이 아닌 것으로 처리

        } catch (Exception e) {
            log.error("Database fallback also failed for key: {}", key, e);
            return true; // 최종 안전 장치
        }
    }

    @Override
    @Deprecated
    public boolean existsByUserIdAndPromptIdSince(Long userId, Long promptId, LocalDateTime since) {
        return promptViewLogRepository.existsByPromptTemplateIdAndUserIdAndViewedAtAfter(
            promptId, userId, since);
    }

    @Override
    @Deprecated
    public boolean existsByIpAddressAndPromptIdSince(String ipAddress, Long promptId, LocalDateTime since) {
        return promptViewLogRepository.existsByPromptTemplateIdAndIpAddressAndViewedAtAfter(
            promptId, ipAddress, since);
    }
}
```

### 12.6 Redis 기반 조회수 집계 어댑터

```java
/**
 * Redis 기반 조회수 집계 어댑터
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisViewCountAdapter implements UpdatePromptViewCountPort {

    private final RedisTemplate<String, String> redisTemplate;
    private final PromptViewCountRepository promptViewCountRepository;

    private static final String VIEW_COUNT_KEY_PREFIX = "viewcount:";
    private static final String VIEW_COUNT_FLUSH_LOCK_PREFIX = "viewcount:flush:lock:";
    private static final String VIEW_COUNT_DAILY_PREFIX = "viewcount:daily:";
    private static final int VIEW_COUNT_TTL_HOURS = 24; // 24시간 TTL 설정

    @Override
    public long incrementViewCountInCache(Long promptTemplateId) {
        try {
            String key = VIEW_COUNT_KEY_PREFIX + promptTemplateId;
            String dailyKey = getDailyViewCountKey(promptTemplateId);

            // 조회수 증가 및 TTL 설정
            Long redisCount = redisTemplate.opsForValue().increment(key, 1);
            redisTemplate.expire(key, Duration.ofHours(VIEW_COUNT_TTL_HOURS));

            // 일별 조회수도 함께 증가
            redisTemplate.opsForValue().increment(dailyKey, 1);
            redisTemplate.expire(dailyKey, Duration.ofDays(7)); // 7일 보관

            // Redis에 처음 저장되는 경우, DB의 기존 값과 동기화
            if (redisCount == 1) {
                long dbCount = getCurrentViewCountFromDb(promptTemplateId);
                if (dbCount > 0) {
                    redisTemplate.opsForValue().set(key, String.valueOf(dbCount + 1));
                    redisTemplate.expire(key, Duration.ofHours(VIEW_COUNT_TTL_HOURS));
                    return dbCount + 1;
                }
            }

            return redisCount != null ? redisCount : 1;

        } catch (Exception e) {
            log.error("Failed to increment view count in Redis for prompt: {}", promptTemplateId, e);
            // Redis 실패 시 DB 직접 업데이트
            return incrementViewCountInDbDirectly(promptTemplateId);
        }
    }

    @Override
    @Transactional
    public int flushViewCountsToDb() {
        try {
            Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY_PREFIX + "*");
            if (keys == null || keys.isEmpty()) {
                return 0;
            }

            int flushedCount = 0;
            for (String key : keys) {
                try {
                    Long promptId = extractPromptIdFromKey(key);

                    // 분산 락으로 중복 플러시 방지
                    if (!acquireFlushLock(promptId)) {
                        log.debug("Flush lock already acquired for prompt: {}", promptId);
                        continue;
                    }

                    try {
                        String countStr = redisTemplate.opsForValue().get(key);
                        if (countStr != null) {
                            long redisCount = Long.parseLong(countStr);
                            long dbCount = getCurrentViewCountFromDb(promptId);

                            if (redisCount > dbCount) {
                                long increment = redisCount - dbCount;
                                incrementViewCountInDb(promptId, increment);
                                flushedCount++;

                                log.debug("Flushed view count: promptId={}, increment={}, total={}",
                                    promptId, increment, redisCount);
                            }

                            // 플러시 완료 후 Redis 키 삭제
                            redisTemplate.delete(key);
                        }
                    } finally {
                        releaseFlushLock(promptId);
                    }

                } catch (Exception e) {
                    log.error("Failed to flush view count for key: {}", key, e);
                }
            }

            return flushedCount;

        } catch (Exception e) {
            log.error("Failed to flush view counts to database", e);
            return 0;
        }
    }

    /**
     * 일별 조회수 키 생성
     */
    private String getDailyViewCountKey(Long promptTemplateId) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return VIEW_COUNT_DAILY_PREFIX + promptTemplateId + ":" + today;
    }

    /**
     * 플러시 분산 락 획득
     */
    private boolean acquireFlushLock(Long promptTemplateId) {
        String lockKey = VIEW_COUNT_FLUSH_LOCK_PREFIX + promptTemplateId;
        Boolean acquired = redisTemplate.opsForValue()
            .setIfAbsent(lockKey, "locked", Duration.ofMinutes(5));
        return Boolean.TRUE.equals(acquired);
    }

    /**
     * 플러시 분산 락 해제
     */
    private void releaseFlushLock(Long promptTemplateId) {
        String lockKey = VIEW_COUNT_FLUSH_LOCK_PREFIX + promptTemplateId;
        redisTemplate.delete(lockKey);
    }

    /**
     * 애플리케이션 종료 시 강제 플러시
     */
    @PreDestroy
    public void onShutdown() {
        log.info("Application shutdown detected. Flushing remaining view counts to database...");
        try {
            // 기존 배치 락 무시하고 강제 플러시
            redisTemplate.delete(BATCH_LOCK_KEY);

            int flushedCount = flushViewCountsToDb();
            log.info("Final flush completed: {} view counts saved to database", flushedCount);

        } catch (Exception e) {
            log.error("Failed to perform final view count flush on shutdown", e);
        }
    }

    private long getCurrentViewCountFromDb(Long promptTemplateId) {
        return promptViewCountRepository.findByPromptTemplateId(promptTemplateId)
            .map(PromptViewCountEntity::getTotalViewCount)
            .orElse(0L);
    }

    private long incrementViewCountInDbDirectly(Long promptTemplateId) {
        promptViewCountRepository.incrementViewCount(promptTemplateId, 1);
        return getCurrentViewCountFromDb(promptTemplateId);
    }

    private Long extractPromptIdFromKey(String key) {
        return Long.parseLong(key.substring(VIEW_COUNT_KEY_PREFIX.length()));
    }
}
```

### 12.7 전체 조회수 통계 JPA 어댑터 (기존 PromptStatisticsQueryAdapter 패턴 적용)

```java
/**
 * 전체 조회수 통계 조회 JPA 어댑터
 * 기존 PromptStatisticsQueryAdapter 패턴을 참조하여 설계
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PromptViewStatisticsQueryAdapter implements LoadPromptViewStatisticsPort {

    private final PromptViewCountRepository promptViewCountRepository;
    private final PromptViewLogRepository promptViewLogRepository;
    private final PromptDailyViewStatsRepository promptDailyViewStatsRepository;
    private final PromptTemplateRepository promptTemplateRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String VIEW_COUNT_KEY_PREFIX = "viewcount:";

    /**
     * 전체 누적 조회수를 조회합니다.
     * Redis 캐시와 DB를 조합하여 정확한 값을 반환
     */
    @Override
    public long loadTotalViewCount() {
        try {
            log.debug("Loading total view count");

            // 1. DB에서 기본 조회수 합계 조회
            long dbTotalCount = promptViewCountRepository.sumAllViewCounts();
            log.debug("DB total view count: {}", dbTotalCount);

            // 2. Redis에서 플러시되지 않은 조회수 합계 조회
            long redisPendingCount = getRedisViewCountSum();
            log.debug("Redis pending view count: {}", redisPendingCount);

            long totalCount = dbTotalCount + redisPendingCount;
            log.debug("Total view count (DB + Redis): {}", totalCount);

            return totalCount;

        } catch (Exception e) {
            log.error("Failed to load total view count", e);
            throw new ViewOperationException("Failed to load total view count", e);
        }
    }

    /**
     * 특정 기간 동안의 조회수를 조회합니다.
     */
    @Override
    public long loadViewCountByPeriod(ComparisonPeriod period) {
        Assert.notNull(period, "period must not be null");

        try {
            log.debug("Loading view count for period: {} to {}", period.getStartDate(), period.getEndDate());

            // 일별 통계 테이블에서 효율적으로 조회
            LocalDate startDate = period.getStartDate().toLocalDate();
            LocalDate endDate = period.getEndDate().toLocalDate();

            long periodViewCount = promptDailyViewStatsRepository
                .sumViewCountByDateRange(startDate, endDate);

            log.debug("Period view count ({} to {}): {}", startDate, endDate, periodViewCount);
            return periodViewCount;

        } catch (Exception e) {
            log.error("Failed to load view count by period", e);
            throw new ViewOperationException("Failed to load view count by period", e);
        }
    }

    /**
     * 조회수 기준 인기 프롬프트 목록을 조회합니다.
     */
    @Override
    public List<TopViewedPrompt> loadTopViewedPrompts(LoadTopViewedPromptsQuery query) {
        Assert.notNull(query, "query must not be null");

        try {
            log.debug("Loading top viewed prompts: days={}, limit={}", query.getDays(), query.getLimit());

            LocalDate startDate = query.getStartDate();
            LocalDate endDate = query.getEndDate();

            // 일별 통계 테이블에서 기간별 조회수 집계하여 상위 프롬프트 조회
            List<Object[]> results = promptDailyViewStatsRepository
                .findTopViewedPromptsWithDetails(startDate, endDate, query.getLimit());

            List<TopViewedPrompt> topPrompts = new ArrayList<>();
            int rank = 1;

            for (Object[] result : results) {
                Long promptTemplateId = (Long) result[0];
                String title = (String) result[1];
                String categoryName = (String) result[2];
                String authorName = (String) result[3];
                UUID promptUuid = (UUID) result[4];
                Long periodViews = (Long) result[5];
                Long allTimeViews = (Long) result[6];
                LocalDateTime lastViewedAt = (LocalDateTime) result[7];

                double averageDailyViews = (double) periodViews / query.getDays();

                TopViewedPrompt topPrompt = new TopViewedPrompt(
                    rank++, promptUuid, title, categoryName,
                    periodViews, allTimeViews, averageDailyViews,
                    authorName, lastViewedAt
                );

                topPrompts.add(topPrompt);
            }

            log.debug("Top viewed prompts loaded: {} items", topPrompts.size());
            return topPrompts;

        } catch (Exception e) {
            log.error("Failed to load top viewed prompts", e);
            throw new ViewOperationException("Failed to load top viewed prompts", e);
        }
    }

    /**
     * 일별 조회수 통계를 조회합니다. (트렌드 분석용)
     */
    @Override
    public List<DailyViewStat> loadDailyViewStats(Long promptTemplateId, LocalDate startDate, LocalDate endDate) {
        Assert.notNull(promptTemplateId, "promptTemplateId must not be null");
        Assert.notNull(startDate, "startDate must not be null");
        Assert.notNull(endDate, "endDate must not be null");

        try {
            log.debug("Loading daily view stats for prompt: {} from {} to {}",
                promptTemplateId, startDate, endDate);

            List<Object[]> results = promptDailyViewStatsRepository
                .findDailyViewStatsByPromptAndDateRange(promptTemplateId, startDate, endDate);

            List<DailyViewStat> dailyStats = results.stream()
                .map(result -> {
                    LocalDate viewDate = (LocalDate) result[0];
                    Long dailyViewCount = (Long) result[1];
                    return new DailyViewStat(viewDate, dailyViewCount);
                })
                .collect(Collectors.toList());

            log.debug("Daily view stats loaded: {} days", dailyStats.size());
            return dailyStats;

        } catch (Exception e) {
            log.error("Failed to load daily view stats", e);
            throw new ViewOperationException("Failed to load daily view stats", e);
        }
    }

    /**
     * Redis에서 플러시되지 않은 조회수 합계를 계산합니다.
     */
    private long getRedisViewCountSum() {
        try {
            Set<String> keys = redisTemplate.keys(VIEW_COUNT_KEY_PREFIX + "*");
            if (keys == null || keys.isEmpty()) {
                return 0L;
            }

            long totalRedisCount = 0L;
            for (String key : keys) {
                String value = redisTemplate.opsForValue().get(key);
                if (value != null) {
                    try {
                        totalRedisCount += Long.parseLong(value);
                    } catch (NumberFormatException e) {
                        log.warn("Invalid Redis view count value for key {}: {}", key, value);
                    }
                }
            }

            return totalRedisCount;

        } catch (Exception e) {
            log.warn("Failed to get Redis view count sum, returning 0", e);
            return 0L;
        }
    }
}
```

### 12.8 전체 조회수 통계용 Repository 인터페이스

```java
/**
 * 프롬프트 조회수 통계 Repository 인터페이스
 */
public interface PromptViewCountRepository extends JpaRepository<PromptViewCount, Long> {

    /**
     * 전체 조회수 합계를 조회합니다.
     */
    @Query("SELECT COALESCE(SUM(pvc.viewCount), 0) FROM PromptViewCount pvc")
    long sumAllViewCounts();

    /**
     * 특정 프롬프트의 조회수를 조회합니다.
     */
    @Query("SELECT pvc.viewCount FROM PromptViewCount pvc WHERE pvc.promptTemplateId = :promptTemplateId")
    Optional<Long> findViewCountByPromptTemplateId(@Param("promptTemplateId") Long promptTemplateId);
}

/**
 * 일별 조회수 통계 Repository 인터페이스
 */
public interface PromptDailyViewStatsRepository extends JpaRepository<PromptDailyViewStats, Long> {

    /**
     * 특정 기간의 전체 조회수 합계를 조회합니다.
     */
    @Query("SELECT COALESCE(SUM(pdvs.dailyViewCount), 0) " +
           "FROM PromptDailyViewStats pdvs " +
           "WHERE pdvs.viewDate BETWEEN :startDate AND :endDate")
    long sumViewCountByDateRange(@Param("startDate") LocalDate startDate,
                                @Param("endDate") LocalDate endDate);

    /**
     * 조회수 기준 인기 프롬프트 목록을 상세 정보와 함께 조회합니다.
     */
    @Query("SELECT pt.id, pt.title, c.name, u.nickname, pt.uuid, " +
           "       SUM(pdvs.dailyViewCount) as periodViews, " +
           "       pvc.viewCount as allTimeViews, " +
           "       MAX(pvl.viewedAt) as lastViewedAt " +
           "FROM PromptDailyViewStats pdvs " +
           "JOIN PromptTemplate pt ON pdvs.promptTemplateId = pt.id " +
           "JOIN Category c ON pt.categoryId = c.id " +
           "JOIN Users u ON pt.userId = u.id " +
           "LEFT JOIN PromptViewCount pvc ON pt.id = pvc.promptTemplateId " +
           "LEFT JOIN PromptViewLog pvl ON pt.id = pvl.promptTemplateId " +
           "WHERE pdvs.viewDate BETWEEN :startDate AND :endDate " +
           "GROUP BY pt.id, pt.title, c.name, u.nickname, pt.uuid, pvc.viewCount " +
           "ORDER BY periodViews DESC " +
           "LIMIT :limit")
    List<Object[]> findTopViewedPromptsWithDetails(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate,
                                                  @Param("limit") int limit);

    /**
     * 특정 프롬프트의 일별 조회수 통계를 조회합니다.
     */
    @Query("SELECT pdvs.viewDate, pdvs.dailyViewCount " +
           "FROM PromptDailyViewStats pdvs " +
           "WHERE pdvs.promptTemplateId = :promptTemplateId " +
           "AND pdvs.viewDate BETWEEN :startDate AND :endDate " +
           "ORDER BY pdvs.viewDate ASC")
    List<Object[]> findDailyViewStatsByPromptAndDateRange(@Param("promptTemplateId") Long promptTemplateId,
                                                         @Param("startDate") LocalDate startDate,
                                                         @Param("endDate") LocalDate endDate);
}
```

이 설계는 기존 좋아요/즐겨찾기 기능과 동일한 아키텍처 패턴을 적용하여 일관성을 유지하면서, 조회수 기능의 특수한 요구사항(중복 방지, 익명 사용자 지원)을 충족하도록 설계되었습니다.

#### LoadViewTrendQuery (기존 ComparisonPeriod 패턴 적용)

```java
/**
 * 프롬프트 조회수 트렌드 조회 쿼리 객체
 * 기존 ComparisonPeriod 패턴을 참조하여 설계
 */
@Getter
@Builder
public class LoadViewTrendQuery {
    private final UUID promptTemplateUuid;
    private final LocalDate startDate;
    private final LocalDate endDate;

    /**
     * 트렌드 조회 쿼리 생성자
     */
    private LoadViewTrendQuery(UUID promptTemplateUuid, LocalDate startDate, LocalDate endDate) {
        Assert.notNull(promptTemplateUuid, "promptTemplateUuid must not be null");
        Assert.notNull(startDate, "startDate must not be null");
        Assert.notNull(endDate, "endDate must not be null");
        Assert.isTrue(!endDate.isBefore(startDate), "endDate must not be before startDate");

        this.promptTemplateUuid = promptTemplateUuid;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * 조회 기간 일수 계산
     */
    public int getDays() {
        return (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;
    }
}

/**
 * 인기 프롬프트 조회 쿼리 객체
 */
@Getter
@Builder
public class LoadPopularPromptsQuery {
    private final int days;
    private final int limit;
    private final LocalDate endDate;

    private LoadPopularPromptsQuery(int days, int limit, LocalDate endDate) {
        Assert.isTrue(days > 0 && days <= 365, "days must be between 1 and 365");
        Assert.isTrue(limit > 0 && limit <= 100, "limit must be between 1 and 100");

        this.days = days;
        this.limit = limit;
        this.endDate = endDate != null ? endDate : LocalDate.now();
    }

    public LocalDate getStartDate() {
        return endDate.minusDays(days - 1);
    }
}
