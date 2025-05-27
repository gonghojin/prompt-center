package com.gongdel.promptserver.application.port.in.command;

import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.User;
import com.gongdel.promptserver.domain.model.Visibility;
import com.gongdel.promptserver.domain.model.InputVariable;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.*;

/**
 * 프롬프트 등록 커맨드 객체입니다. 프롬프트 템플릿 생성에 필요한 모든 정보를 캡슐화합니다.
 */
@Getter
public class RegisterPromptCommand {

    /**
     * 프롬프트 템플릿의 제목
     */
    @NonNull
    private final String title;

    /**
     * 프롬프트 템플릿의 설명
     */
    private final String description;

    /**
     * 프롬프트 템플릿의 내용 (첫 버전에 사용됨)
     */
    @NonNull
    private final String content;

    /**
     * 프롬프트 템플릿의 작성자
     */
    @NonNull
    private final User createdBy;

    /**
     * 프롬프트 템플릿에 연결된 태그 목록
     */
    private final Set<String> tags;

    /**
     * 프롬프트 템플릿의 가시성
     */
    private final Visibility visibility;

    /**
     * 프롬프트 템플릿의 카테고리 ID
     */
    private final Long categoryId;

    /**
     * 프롬프트 템플릿의 입력 변수 목록 (타입, 설명, 필수여부, 기본값 포함)
     */
    private final List<InputVariable> inputVariables;

    /**
     * 프롬프트 템플릿의 상태
     */
    private final PromptStatus status;

    @Builder
    public RegisterPromptCommand(
            @NonNull String title,
            String description,
            @NonNull String content,
            @NonNull User createdBy,
            Set<String> tags,
            Visibility visibility,
            Long categoryId,
            List<InputVariable> inputVariables,
            PromptStatus status) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.createdBy = createdBy;
        // 불변 컬렉션으로 변환하여 불변성 보장
        this.tags = tags != null ? Collections.unmodifiableSet(new HashSet<>(tags)) : Collections.emptySet();
        this.visibility = visibility;

        // categoryId 유효성 검사
        validateCategoryId(categoryId);
        this.categoryId = categoryId;

        this.inputVariables = inputVariables != null ? Collections.unmodifiableList(new ArrayList<>(inputVariables))
                : Collections.emptyList();
        this.status = status;
    }

    /**
     * 기본 프롬프트 생성을 위한 팩토리 메소드
     *
     * @param title     프롬프트 제목
     * @param content   프롬프트 내용
     * @param createdBy 작성자
     * @return 생성된 커맨드 객체
     */
    public static RegisterPromptCommand createBasic(
            @NonNull String title,
            @NonNull String content,
            @NonNull User createdBy) {
        return RegisterPromptCommand.builder()
                .title(title)
                .content(content)
                .createdBy(createdBy)
                .status(PromptStatus.DRAFT)
                .build();
    }

    /**
     * 팀 공유용 프롬프트 생성을 위한 팩토리 메소드
     *
     * @param title     프롬프트 제목
     * @param content   프롬프트 내용
     * @param createdBy 작성자
     * @return 생성된 커맨드 객체
     */
    public static RegisterPromptCommand createForTeam(
            @NonNull String title,
            @NonNull String content,
            @NonNull User createdBy) {
        return RegisterPromptCommand.builder()
                .title(title)
                .content(content)
                .createdBy(createdBy)
                .visibility(Visibility.TEAM)
                .status(PromptStatus.DRAFT)
                .build();
    }

    /**
     * 특정 카테고리에 속한 프롬프트 생성을 위한 팩토리 메소드
     *
     * @param title      프롬프트 제목
     * @param content    프롬프트 내용
     * @param createdBy  작성자
     * @param categoryId 카테고리 ID
     * @return 생성된 커맨드 객체
     */
    public static RegisterPromptCommand createWithCategory(
            @NonNull String title,
            @NonNull String content,
            @NonNull User createdBy,
            Long categoryId) {
        return RegisterPromptCommand.builder()
                .title(title)
                .content(content)
                .createdBy(createdBy)
                .categoryId(categoryId)
                .status(PromptStatus.DRAFT)
                .build();
    }

    /**
     * 카테고리 ID의 유효성을 검증합니다.
     *
     * @param categoryId 검증할 카테고리 ID
     * @throws IllegalArgumentException 유효성 검증에 실패한 경우
     */
    private void validateCategoryId(Long categoryId) {
        // 카테고리 ID가 0보다 작거나 같은 경우 예외 발생
        if (categoryId != null && categoryId <= 0) {
            throw new IllegalArgumentException("카테고리 ID는 양수여야 합니다: " + categoryId);
        }
    }
}
