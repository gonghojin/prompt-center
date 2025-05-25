package com.gongdel.promptserver.domain.model;

import com.gongdel.promptserver.domain.exception.PromptValidationException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 프롬프트 템플릿을 나타내는 도메인 모델 클래스입니다. 모델 문서에 정의된 엔티티 구조에 맞춰 구현되었으며, 프롬프트 템플릿의 생성, 수정, 상태 관리 기능을 제공합니다.
 */
@Getter
@ToString(exclude = {"description"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode(of = "id")
public class PromptTemplate extends BaseTimeEntity {

    private static final Logger log = LoggerFactory.getLogger(PromptTemplate.class);

    // 상수 정의
    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_DESCRIPTION_LENGTH = 1000;

    // 필수 필드
    private Long id;
    private UUID uuid;
    private String title;
    private Long currentVersionId;
    private Long categoryId;
    private Long createdById;
    private Visibility visibility;
    private PromptStatus status;
    private String description;
    private List<String> inputVariables;

    // 관계 도메인 객체 필드 추가
    private User createdBy;
    private Category category;
    private List<Tag> tags;
    private PromptVersion version;
    private PromptStats stats;

    /**
     * 모든 필드를 초기화하는 생성자입니다.
     *
     * @param id               프롬프트 고유 식별자
     * @param uuid             외부 노출용 UUID
     * @param title            프롬프트 제목
     * @param currentVersionId 현재 사용 중인 버전 ID
     * @param categoryId       카테고리 ID
     * @param createdById      생성자 ID
     * @param visibility       가시성(공개/팀/비공개)
     * @param status           템플릿 상태
     * @param description      프롬프트 설명
     * @param inputVariables   입력 변수 목록
     * @param createdAt        생성 일시
     * @param updatedAt        수정 일시
     * @param createdBy        생성자 객체
     * @param category         카테고리 객체
     * @param tags             태그 목록
     * @param version          현재 프롬프트 버전
     * @param stats            프롬프트 통계 객체
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    @Builder
    public PromptTemplate(
        Long id,
        UUID uuid,
        String title,
        Long currentVersionId,
        Long categoryId,
        Long createdById,
        Visibility visibility,
        PromptStatus status,
        String description,
        List<String> inputVariables,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        User createdBy,
        Category category,
        List<Tag> tags,
        PromptVersion version,
        PromptStats stats) throws PromptValidationException {
        super(createdAt, updatedAt);

        // 각 필드에 대한 유효성 검증 수행
        validateTitle(title);
        validateCreatedById(createdById);
        validateDescription(description);
        // currentVersionId와 categoryId는 생성 시 null 허용

        this.id = id;
        this.uuid = initializeUuid(uuid);
        this.title = title;
        this.currentVersionId = currentVersionId;
        this.categoryId = categoryId;
        this.createdById = createdById;
        this.visibility = initializeVisibility(visibility);
        this.status = initializeStatus(status);
        this.description = description;
        this.inputVariables = initializeInputVariables(inputVariables);

        // 관계 도메인 객체 초기화
        this.createdBy = createdBy;
        this.category = category;
        this.tags = tags != null ? new ArrayList<>(tags) : new ArrayList<>();
        this.version = version;
        this.stats = stats != null ? stats : new PromptStats();

        log.debug("Created prompt template: id={}, title={}, createdById={}, hasVersion={}",
            this.id, this.title, this.createdById, this.currentVersionId != null);
    }

    /**
     * 프롬프트 템플릿 정보를 업데이트합니다.
     *
     * @param title            업데이트할 제목
     * @param currentVersionId 업데이트할 현재 버전 ID
     * @param categoryId       업데이트할 카테고리 ID
     * @param visibility       업데이트할 가시성
     * @param status           업데이트할 상태
     * @param description      업데이트할 설명
     * @param inputVariables   업데이트할 입력 변수 목록
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    public void update(
        String title,
        Long currentVersionId,
        Long categoryId,
        Visibility visibility,
        PromptStatus status,
        String description,
        List<String> inputVariables) throws PromptValidationException {

        // 유효성 검증은 각 필드별로 개별적으로 수행
        validateTitle(title);
        validateDescription(description);
        // createdById는 업데이트 시 검증하지 않음

        this.title = title;
        this.currentVersionId = currentVersionId;
        this.categoryId = categoryId;
        this.visibility = initializeVisibility(visibility);
        this.status = initializeStatus(status);
        this.description = description;
        updateInputVariables(inputVariables);

        updateModifiedTime();

        log.debug("Updated prompt template: id={}, title={}, status={}, versionId={}, categoryId={}",
            this.id, this.title, this.status, this.currentVersionId, this.categoryId);
    }

    /**
     * UUID를 초기화합니다. null인 경우 새로운 UUID를 생성합니다.
     *
     * @param uuid 초기화할 UUID
     * @return 초기화된 UUID
     */
    private UUID initializeUuid(UUID uuid) {
        return Objects.requireNonNullElseGet(uuid, UUID::randomUUID);
    }

    /**
     * 가시성을 초기화합니다. null인 경우 기본값으로 PRIVATE를 사용합니다.
     *
     * @param visibility 초기화할 가시성
     * @return 초기화된 가시성
     */
    private Visibility initializeVisibility(Visibility visibility) {
        return Objects.requireNonNullElse(visibility, Visibility.PRIVATE);
    }

    /**
     * 상태를 초기화합니다. null인 경우 기본값으로 DRAFT를 사용합니다.
     *
     * @param status 초기화할 상태
     * @return 초기화된 상태
     */
    private PromptStatus initializeStatus(PromptStatus status) {
        return Objects.requireNonNullElse(status, PromptStatus.DRAFT);
    }

    /**
     * 입력 변수 목록을 초기화합니다. null인 경우 빈 리스트를 생성합니다.
     *
     * @param inputVariables 초기화할 입력 변수 목록
     * @return 초기화된 입력 변수 목록
     */
    private List<String> initializeInputVariables(List<String> inputVariables) {
        return inputVariables != null
            ? new ArrayList<>(inputVariables)
            : new ArrayList<>();
    }

    /**
     * 제목의 유효성을 검증합니다.
     *
     * @param title 검증할 제목
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateTitle(String title) throws PromptValidationException {
        if (StringUtils.isBlank(title)) {
            throw new PromptValidationException("제목은 비어있을 수 없습니다");
        }

        if (title.length() > MAX_TITLE_LENGTH) {
            throw new PromptValidationException(
                String.format("제목은 %d자를 초과할 수 없습니다", MAX_TITLE_LENGTH));
        }
    }

    /**
     * 생성자 ID의 유효성을 검증합니다. 생성 시에만 사용됩니다.
     *
     * @param createdById 검증할 생성자 ID
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateCreatedById(Long createdById) throws PromptValidationException {
        // 생성 시에만 검증
        if (createdById == null) {
            throw new PromptValidationException("생성자 ID는 필수입니다");
        }
    }

    /**
     * 현재 버전 ID의 유효성을 검증합니다.
     *
     * @param currentVersionId 검증할 현재 버전 ID
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateCurrentVersionId(Long currentVersionId) throws PromptValidationException {
        // 현재 버전 ID는 생성 시에는 null일 수 있음
        // 이 메서드는 필요에 따라 확장 가능
    }

    /**
     * 카테고리 ID의 유효성을 검증합니다.
     *
     * @param categoryId 검증할 카테고리 ID
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateCategoryId(Long categoryId) throws PromptValidationException {
        // 카테고리 ID는 선택 사항이지만, 특정 비즈니스 로직에 따라 검증 가능
        // 이 메서드는 필요에 따라 확장 가능
    }

    /**
     * 설명의 유효성을 검증합니다.
     *
     * @param description 검증할 설명
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    private void validateDescription(String description) throws PromptValidationException {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            throw new PromptValidationException(
                String.format("설명은 %d자를 초과할 수 없습니다", MAX_DESCRIPTION_LENGTH));
        }
    }

    /**
     * 입력 변수 목록을 업데이트합니다.
     *
     * @param inputVariables 업데이트할 입력 변수 목록
     */
    private void updateInputVariables(List<String> inputVariables) {
        this.inputVariables.clear();
        if (inputVariables != null) {
            this.inputVariables.addAll(inputVariables);
        }
    }

    /**
     * 프롬프트 템플릿의 입력 변수 목록을 불변 리스트로 반환합니다.
     *
     * @return 입력 변수 목록
     */
    public List<String> getInputVariables() {
        return Collections.unmodifiableList(inputVariables);
    }

    /**
     * 프롬프트가 공개인지 여부를 반환합니다.
     *
     * @return 공개 여부
     */
    public boolean isPublic() {
        return visibility == Visibility.PUBLIC;
    }

    /**
     * 프롬프트가 팀에게 공개인지 여부를 반환합니다.
     *
     * @return 팀 공개 여부
     */
    public boolean isTeamVisible() {
        return visibility == Visibility.TEAM;
    }

    /**
     * 현재 버전 ID를 설정합니다.
     *
     * @param versionId 설정할 버전 ID
     * @throws IllegalArgumentException 버전 ID가 null인 경우
     */
    public void setCurrentVersionId(Long versionId) {
        if (versionId == null) {
            throw new IllegalArgumentException("버전 ID는 null이 될 수 없습니다");
        }

        this.currentVersionId = versionId;
        updateModifiedTime();
        log.debug("Updated current version: promptId={}, versionId={}", id, versionId);
    }

    /**
     * 상태를 변경합니다.
     *
     * @param status 변경할 상태
     * @throws IllegalArgumentException 상태가 null인 경우
     */
    public void updateStatus(PromptStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("상태는 null이 될 수 없습니다");
        }

        this.status = status;
        updateModifiedTime();
        log.debug("Updated status: promptId={}, status={}", id, status);
    }

}
