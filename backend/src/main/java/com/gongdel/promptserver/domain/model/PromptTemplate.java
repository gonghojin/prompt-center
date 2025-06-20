package com.gongdel.promptserver.domain.model;

import com.gongdel.promptserver.domain.exception.PromptValidationException;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * 프롬프트 템플릿을 나타내는 도메인 모델 클래스입니다. 모델 문서에 정의된 엔티티 구조에 맞춰 구현되었으며, 프롬프트 템플릿의 생성, 수정,
 * 상태 관리 기능을 제공합니다.
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

    // 태그 연관관계 (불변성 보장 위해 Set 사용)
    private Set<Tag> tags = new HashSet<>();

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
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<Tag> tags) {
        super(createdAt, updatedAt);
        validateTitle(title);
        validateCreatedById(createdById);
        validateDescription(description);
        this.id = id;
        this.uuid = initializeUuid(uuid);
        this.title = title;
        this.currentVersionId = currentVersionId;
        this.categoryId = categoryId;
        this.createdById = createdById;
        this.visibility = initializeVisibility(visibility);
        this.status = initializeStatus(status);
        this.description = description;
        if (tags != null) {
            this.tags = new HashSet<>(tags);
        }
        log.debug("Created prompt template: id={}, title={}, createdById={}, hasVersion={}",
            this.id, this.title, this.createdById, this.currentVersionId != null);
    }

    /**
     * 최초 등록(생성)용 프롬프트 템플릿 생성 팩토리 메서드입니다.
     * currentVersionId는 null로 고정됩니다.
     *
     * @param title       제목
     * @param description 설명
     * @param createdById 생성자 ID
     * @param visibility  가시성
     * @param categoryId  카테고리 ID
     * @param status      상태
     * @param tags        태그 목록
     * @return 최초 등록용 프롬프트 템플릿
     */
    public static PromptTemplate newTemplateForInitialRegistration(
        String title,
        String description,
        Long createdById,
        Visibility visibility,
        Long categoryId,
        PromptStatus status,
        Set<Tag> tags) {
        return new PromptTemplate(
            null, // id
            null, // uuid
            title,
            null, // currentVersionId (최초 등록이므로 null)
            categoryId,
            createdById,
            visibility,
            status,
            description,
            null, // createdAt
            null, // updatedAt
            tags);
    }

    /**
     * 일반 생성/조회용 프롬프트 템플릿 생성 팩토리 메서드입니다.
     *
     * @param id               PK
     * @param uuid             UUID
     * @param title            제목
     * @param currentVersionId 현재 버전 ID
     * @param categoryId       카테고리 ID
     * @param createdById      생성자 ID
     * @param visibility       가시성
     * @param status           상태
     * @param description      설명
     * @param createdAt        생성일
     * @param updatedAt        수정일
     * @param tags             태그 목록
     * @return 프롬프트 템플릿
     */
    public static PromptTemplate of(
        Long id,
        UUID uuid,
        String title,
        Long currentVersionId,
        Long categoryId,
        Long createdById,
        Visibility visibility,
        PromptStatus status,
        String description,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Set<Tag> tags) {
        if (currentVersionId == null) {
            throw new PromptValidationException("일반 생성/조회 시 currentVersionId는 null이 아니어야 합니다.");
        }
        return new PromptTemplate(
            id, uuid, title, currentVersionId, categoryId, createdById, visibility, status, description, createdAt,
            updatedAt, tags);
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
     * @param tags             업데이트할 태그 목록
     * @throws PromptValidationException 유효성 검증에 실패한 경우
     */
    public void update(
        String title,
        Long currentVersionId,
        Long categoryId,
        Visibility visibility,
        PromptStatus status,
        String description,
        Set<Tag> tags) throws PromptValidationException {

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
        if (tags != null) {
            this.tags.clear();
            this.tags.addAll(tags);
        }

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

    /**
     * 태그 전체 교체
     */
    public void updateTags(Set<Tag> newTags) {
        this.tags.clear();
        if (newTags != null) {
            this.tags.addAll(newTags);
        }
    }

    /**
     * 태그 추가
     */
    public void addTag(Tag tag) {
        if (tag != null) {
            this.tags.add(tag);
        }
    }

    /**
     * 태그 제거
     */
    public void removeTag(Tag tag) {
        if (tag != null) {
            this.tags.remove(tag);
        }
    }

    /**
     * 태그 목록 반환(불변)
     */
    public Set<Tag> getTags() {
        return tags == null ? Set.of() : Set.copyOf(tags);
    }
}
