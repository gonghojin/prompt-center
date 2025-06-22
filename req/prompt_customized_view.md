# 프롬프트 조회수(View) 기능 세부 설계 문서

## 📌 설계 목표

- 프롬프트 상세 페이지 조회 시 자동으로 조회수 증가
- 중복 조회 방지 (1시간 내 동일 사용자/IP 중복 차단)
- 비로그인 사용자도 조회수 집계 포함
- 실시간 조회수 통계 제공
- 기존 좋아요/즐겨찾기와 일관된 아키텍처 적용

---

## 🗄️ 데이터 모델 설계

### 1. PromptViewCount (조회수 집계 테이블)

```sql
-- 프롬프트별 누적 조회수 집계 테이블
CREATE TABLE prompt_view_counts (
    prompt_template_id BIGINT PRIMARY KEY,      -- 프롬프트 템플릿 ID (FK)
    total_view_count   BIGINT NOT NULL DEFAULT 0,  -- 누적 조회수
    updated_at         TIMESTAMP,                   -- 마지막 업데이트 시간

    CONSTRAINT fk_prompt_view_count_template
        FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates(id)
);

-- 인덱스
CREATE INDEX idx_prompt_view_count_template_id ON prompt_view_counts(prompt_template_id);
```

### 2. PromptViewLog (조회 상세 로그 테이블)

```sql
-- 조회 이력 및 중복 방지용 로그 테이블
CREATE TABLE prompt_view_logs (
    id                 UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prompt_template_id BIGINT NOT NULL,              -- 프롬프트 템플릿 ID (FK)
    user_id            BIGINT,                       -- 사용자 ID (FK, Nullable)
    ip_address         VARCHAR(45),                  -- IP 주소 (IPv4/IPv6 지원)
    viewed_at          TIMESTAMP NOT NULL DEFAULT now(), -- 조회 시간

    CONSTRAINT fk_prompt_view_log_template
        FOREIGN KEY (prompt_template_id) REFERENCES prompt_templates(id),
    CONSTRAINT fk_prompt_view_log_user
        FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 인덱스 (중복 방지 및 조회 성능 최적화)
CREATE INDEX idx_prompt_view_log_template_user_time
    ON prompt_view_logs(prompt_template_id, user_id, viewed_at);
CREATE INDEX idx_prompt_view_log_template_ip_time
    ON prompt_view_logs(prompt_template_id, ip_address, viewed_at);
CREATE INDEX idx_prompt_view_log_viewed_at
    ON prompt_view_logs(viewed_at); -- TTL 삭제용
```

---

## 🏗️ 도메인 모델 설계

### 1. PromptViewCount (조회수 집계 도메인)

```java
/**
 * 프롬프트 조회수 집계 도메인 모델
 * BaseTimeEntity를 상속받아 생성/수정 시간을 자동 관리합니다.
 */
@Getter
@ToString
@Builder
public class PromptViewCount extends BaseTimeEntity {
    private final Long promptTemplateId;    // 프롬프트 템플릿 ID
    private final Long totalViewCount;      // 누적 조회수

    /**
     * 조회수 증가
     */
    public PromptViewCount incrementViewCount() {
        return PromptViewCount.builder()
            .promptTemplateId(this.promptTemplateId)
            .totalViewCount(this.totalViewCount + 1)
            .createdAt(this.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 초기 조회수 생성 (신규 프롬프트용)
     */
    public static PromptViewCount createInitial(Long promptTemplateId) {
        return PromptViewCount.builder()
            .promptTemplateId(promptTemplateId)
            .totalViewCount(1L)
            .build();
    }
}
```

### 2. PromptViewLog (조회 로그 도메인)

```java
/**
 * 프롬프트 조회 로그 도메인 모델
 */
@Getter
@ToString
@Builder
public class PromptViewLog {
    private final UUID id;                  // 로그 고유 ID
    private final Long promptTemplateId;    // 프롬프트 템플릿 ID
    private final Long userId;              // 사용자 ID (nullable)
    private final String ipAddress;         // IP 주소
    private final LocalDateTime viewedAt;   // 조회 시간

    /**
     * 로그인 사용자 조회 로그 생성
     */
    public static PromptViewLog createForUser(Long promptTemplateId, Long userId, String ipAddress) {
        return PromptViewLog.builder()
            .id(UUID.randomUUID())
            .promptTemplateId(promptTemplateId)
            .userId(userId)
            .ipAddress(ipAddress)
            .viewedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 비로그인 사용자 조회 로그 생성
     */
    public static PromptViewLog createForGuest(Long promptTemplateId, String ipAddress) {
        return PromptViewLog.builder()
            .id(UUID.randomUUID())
            .promptTemplateId(promptTemplateId)
            .userId(null)
            .ipAddress(ipAddress)
            .viewedAt(LocalDateTime.now())
            .build();
    }
}
```

### 3. ViewDuplicationChecker (중복 조회 체크)

```java
/**
 * 조회수 중복 방지 검증기
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ViewDuplicationChecker {

    private final LoadViewLogPort loadViewLogPort;

    /**
     * 로그인 사용자 중복 조회 체크
     * @param promptTemplateId 프롬프트 ID
     * @param userId 사용자 ID
     * @return 중복 여부 (true: 중복, false: 신규)
     */
    public boolean isDuplicateViewForUser(Long promptTemplateId, Long userId) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return loadViewLogPort.existsByPromptTemplateIdAndUserIdAndViewedAtAfter(
            promptTemplateId, userId, oneHourAgo);
    }

    /**
     * 비로그인 사용자 IP 기반 중복 조회 체크
     * @param promptTemplateId 프롬프트 ID
     * @param ipAddress IP 주소
     * @return 중복 여부 (true: 중복, false: 신규)
     */
    public boolean isDuplicateViewForIp(Long promptTemplateId, String ipAddress) {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return loadViewLogPort.existsByPromptTemplateIdAndIpAddressAndViewedAtAfter(
            promptTemplateId, ipAddress, oneHourAgo);
    }
}
```

---

## 🎯 Use Case 설계

### 1. Command 객체 설계

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

    /**
     * 로그인 사용자용 조회 기록 커맨드 생성자
     */
    @Builder
    private RecordViewCommand(Long userId, UUID promptTemplateUuid, String ipAddress) {
        Assert.notNull(promptTemplateUuid, "promptTemplateUuid must not be null");
        Assert.hasText(ipAddress, "ipAddress must not be blank");

        this.userId = userId;
        this.promptTemplateUuid = promptTemplateUuid;
        this.ipAddress = ipAddress;
    }

    /**
     * 로그인 사용자용 조회 기록 커맨드 생성 팩토리 메서드
     */
    public static RecordViewCommand forUser(Long userId, UUID promptTemplateUuid, String ipAddress) {
        Assert.notNull(userId, "userId must not be null for authenticated user");
        return new RecordViewCommand(userId, promptTemplateUuid, ipAddress);
    }

    /**
     * 비로그인 사용자용 조회 기록 커맨드 생성 팩토리 메서드
     */
    public static RecordViewCommand forGuest(UUID promptTemplateUuid, String ipAddress) {
        return new RecordViewCommand(null, promptTemplateUuid, ipAddress);
    }

    /**
     * 로그인 사용자인지 확인
     */
    public boolean isLoggedInUser() {
        return userId != null;
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

### 2. Command (조회수 증가)

#### PromptViewCommandUseCase

```java
/**
 * 프롬프트 조회수 증가 유스케이스
 */
public interface PromptViewCommandUseCase {
    /**
     * 프롬프트 조회수 증가
     * @param command 조회 기록 커맨드
     * @return 조회 기록 성공 여부
     */
    boolean recordView(RecordViewCommand command);
}
```

#### PromptViewCommandService

```java
/**
 * 프롬프트 조회수 증가 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PromptViewCommandService implements PromptViewCommandUseCase {

    private final LoadPromptTemplateIdPort loadPromptTemplateIdPort;
    private final ViewDuplicationChecker viewDuplicationChecker;
    private final SaveViewLogPort saveViewLogPort;
    private final UpdateViewCountPort updateViewCountPort;

    @Override
    public boolean recordView(RecordViewCommand command) {
        Assert.notNull(command, "RecordViewCommand must not be null");

        log.debug("Recording view: userId={}, promptUuid={}, ip={}",
            command.getUserId(), command.getPromptTemplateUuid(), command.getIpAddress());

        try {
            Long promptId = findPromptIdOrThrow(command.getPromptTemplateUuid());

            // 중복 조회 체크
            if (isDuplicateView(command, promptId)) {
                log.debug("Duplicate view detected: promptId={}, userId={}",
                    promptId, command.getUserId());
                return false;
            }

            // 조회 로그 저장
            PromptViewLog viewLog = createViewLog(command, promptId);
            saveViewLogPort.save(viewLog);

            // 조회수 증가
            updateViewCountPort.incrementViewCount(promptId);

            log.info("View recorded successfully: userId={}, promptUuid={}",
                command.getUserId(), command.getPromptTemplateUuid());
            return true;

        } catch (Exception e) {
            log.error("Failed to record view: promptUuid={}, userId={}, error={}",
                command.getPromptTemplateUuid(), command.getUserId(), e.getMessage(), e);
            throw new ViewOperationException("Failed to record view", e);
        }
    }

    private boolean isDuplicateView(RecordViewCommand command, Long promptId) {
        if (command.isLoggedInUser()) {
            return viewDuplicationChecker.isDuplicateViewForUser(promptId, command.getUserId());
        } else {
            return viewDuplicationChecker.isDuplicateViewForIp(promptId, command.getIpAddress());
        }
    }

    private PromptViewLog createViewLog(RecordViewCommand command, Long promptId) {
        if (command.isLoggedInUser()) {
            return PromptViewLog.createForUser(promptId, command.getUserId(), command.getIpAddress());
        } else {
            return PromptViewLog.createForGuest(promptId, command.getIpAddress());
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

### 3. Query (조회수 조회)

#### PromptViewQueryUseCase

```java
/**
 * 프롬프트 조회수 조회 유스케이스
 */
public interface PromptViewQueryUseCase {
    /**
     * 프롬프트별 조회수 조회
     * @param query 조회수 조회 쿼리
     * @return 조회수 정보
     */
    ViewCount getViewCount(LoadViewCountQuery query);

    /**
     * 여러 프롬프트의 조회수 일괄 조회
     * @param promptTemplateUuids 프롬프트 UUID 목록
     * @return 프롬프트별 조회수 맵
     */
    Map<UUID, Long> getViewCounts(List<UUID> promptTemplateUuids);
}
```

---

## 🌐 REST API 설계

### 1. 조회수 증가 API

```java
/**
 * 프롬프트 조회수 Command API 컨트롤러
 */
@Slf4j
@Tag(name = "프롬프트 조회수 - Command", description = "프롬프트 조회수 증가 API")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptViewCommandController {

    private final PromptViewCommandUseCase promptViewCommandUseCase;
    private final CurrentUserProvider currentUserProvider;

    @Operation(summary = "프롬프트 조회수 증가", description = "프롬프트 상세 조회 시 조회수를 1 증가시킵니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회수 증가 성공"),
        @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @PostMapping("/{id}/view")
    public ResponseEntity<ViewCountResponse> incrementViewCount(
        @Parameter(description = "프롬프트 UUID", required = true)
        @PathVariable("id") UUID id,
        HttpServletRequest request) {

        Assert.notNull(id, "Prompt ID cannot be null");
        String ipAddress = getClientIpAddress(request);

        Long userId = currentUserProvider.getCurrentUserIdOrNull();
        boolean success;

        if (userId != null) {
            log.info("Incrementing view count for user: userId={}, promptUuid={}, ip={}",
                userId, id, ipAddress);
            success = promptViewCommandUseCase.recordView(RecordViewCommand.forUser(userId, id, ipAddress));
        } else {
            log.info("Incrementing view count for guest: promptUuid={}, ip={}", id, ipAddress);
            success = promptViewCommandUseCase.recordView(RecordViewCommand.forGuest(id, ipAddress));
        }

        return success ? ResponseEntity.ok(ViewCountResponse.of(getCurrentViewCount(id)))
                       : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    /**
     * 클라이언트 IP 주소 추출 (Proxy 환경 고려)
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String[] headerNames = {
            "X-Forwarded-For",
            "X-Real-IP",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR"
        };

        for (String headerName : headerNames) {
            String ip = request.getHeader(headerName);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // X-Forwarded-For의 경우 첫 번째 IP 사용
                return ip.split(",")[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}
```

### 2. 조회수 조회 API

```java
/**
 * 프롬프트 조회수 Query API 컨트롤러
 */
@Slf4j
@Tag(name = "프롬프트 조회수 - Query", description = "프롬프트 조회수 조회 API")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptViewQueryController {

    private final PromptViewQueryUseCase promptViewQueryUseCase;

    @Operation(summary = "프롬프트 조회수 조회", description = "특정 프롬프트의 조회수를 조회합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "조회수 조회 성공"),
        @ApiResponse(responseCode = "404", description = "프롬프트를 찾을 수 없음")
    })
    @GetMapping("/{id}/views")
    public ResponseEntity<ViewCountResponse> getViewCount(
        @Parameter(description = "프롬프트 UUID", required = true)
        @PathVariable("id") UUID id) {

        Assert.notNull(id, "Prompt ID cannot be null");
        log.info("Querying view count for prompt: {}", id);

        ViewCount viewCount = promptViewQueryUseCase.getViewCount(LoadViewCountQuery.of(id));
        return ResponseEntity.ok(ViewCountResponse.of(viewCount.getViewCount()));
    }
}
```

### 3. 응답 DTO

```java
/**
 * 조회수 응답 DTO
 */
@Getter
@Builder
@Schema(description = "조회수 응답 DTO")
public class ViewCountResponse {
    @Schema(description = "조회수", example = "1234")
    private final long viewCount;

    public static ViewCountResponse of(long viewCount) {
        return ViewCountResponse.builder()
            .viewCount(viewCount)
            .build();
    }
}
```

---

## 🔌 Port 인터페이스 설계

### 1. Out-bound Ports (Command)

```java
// 조회 로그 저장
public interface SaveViewLogPort {
    void save(PromptViewLog viewLog);
}

// 조회수 업데이트
public interface UpdateViewCountPort {
    long incrementViewCount(Long promptTemplateId);
    long getCurrentViewCount(Long promptTemplateId);
}

// 조회 로그 중복 체크
public interface LoadViewLogPort {
    boolean existsByPromptTemplateIdAndUserIdAndViewedAtAfter(
        Long promptTemplateId, Long userId, LocalDateTime after);
    boolean existsByPromptTemplateIdAndIpAddressAndViewedAtAfter(
        Long promptTemplateId, String ipAddress, LocalDateTime after);
}
```

### 2. Out-bound Ports (Query)

```java
// 조회수 조회
public interface LoadViewCountPort {
    Optional<PromptViewCount> findByPromptTemplateId(Long promptTemplateId);

    Map<Long, Long> findViewCountsByPromptTemplateIds(List<Long> promptTemplateIds);
}
```

---

## ⚠️ 예외 처리 설계

### 1. ViewOperationException

```java
/**
 * 프롬프트 조회수 관련 비즈니스 예외
 */
@Getter
public class ViewOperationException extends BaseException {

    public ViewOperationException(String message) {
        super(ViewErrorType.INTERNAL_SERVER_ERROR, message);
    }

    public ViewOperationException(String message, Throwable cause) {
        super(ViewErrorType.INTERNAL_SERVER_ERROR, message, cause);
    }

    public static ViewOperationException notFound(UUID promptTemplateUuid) {
        return new ViewOperationException(
            String.format("존재하지 않는 프롬프트 UUID: %s", promptTemplateUuid));
    }
}
```

### 2. ViewErrorType

```java
/**
 * 프롬프트 조회수 관련 오류 코드 정의
 */
public enum ViewErrorType implements ErrorCode {
    INTERNAL_SERVER_ERROR(1600, "Internal server error"),
    VIEW_NOT_FOUND(1601, "View count not found"),
    DUPLICATE_VIEW_LOG(1602, "Duplicate view log")

    // ... 구현 생략
}
```

---

## 🔧 구현 우선순위

### Phase 1: 핵심 기능 구현

1. **도메인 모델 구현**
    - PromptViewCount, PromptViewLog 도메인 객체
    - ViewDuplicationChecker 중복 방지 로직

2. **데이터베이스 마이그레이션**
    - prompt_view_counts, prompt_view_logs 테이블 생성
    - 인덱스 및 외래키 제약조건 설정

3. **Use Case 구현**
    - PromptViewCommandService (조회수 증가)
    - PromptViewQueryService (조회수 조회)

### Phase 2: API 및 연동

4. **REST API 구현**
    - POST /api/v1/prompts/{id}/view (조회수 증가)
    - GET /api/v1/prompts/{id}/views (조회수 조회)

5. **Adapter 구현**
    - JPA Repository 어댑터 구현
    - Port 인터페이스 구현

### Phase 3: 최적화 및 운영

6. **성능 최적화**
    - Redis 캐싱 적용 (조회수 집계)
    - 배치 처리 (로그 정리)

7. **통합 테스트**
    - 단위 테스트 및 통합 테스트
    - 성능 테스트 및 부하 테스트

---

## 🚀 프론트엔드 연동 고려사항

### 1. 자동 조회수 증가

- 프롬프트 상세 페이지 진입 시 자동 API 호출
- 비동기 처리로 페이지 로딩 속도 영향 최소화

### 2. 실시간 조회수 표시

- 프롬프트 카드/목록에서 조회수 실시간 표시
- 캐싱을 통한 빠른 응답 제공

### 3. 통계 대시보드 연동

- 인기 프롬프트 (조회수 기준 정렬)
- 카테고리별/기간별 조회수 통계

---

## 📊 모니터링 및 운영

### 1. 로그 관리

- 조회 로그 TTL 설정 (예: 3개월)
- 주기적 로그 정리 배치 작업

### 2. 성능 모니터링

- 조회수 증가 API 응답 시간 모니터링
- 중복 체크 쿼리 성능 모니터링

### 3. 데이터 정합성

- 집계 테이블과 로그 테이블 간 정합성 검증
- 주기적 데이터 동기화 작업

이 설계는 기존 좋아요/즐겨찾기 기능과 일관된 아키텍처를 유지하면서, 조회수 기능의 특수성(중복 방지, 비로그인 사용자 지원)을 고려하여 설계되었습니다.
