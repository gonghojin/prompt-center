package com.gongdel.promptserver.application.port.in.command;

import com.gongdel.promptserver.domain.model.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * 카테고리 생성을 위한 명령 객체입니다.
 */
@Getter
@Builder
public class CreateCategoryCommand {

    /**
     * 카테고리 고유 이름
     */
    @NonNull
    private final String name;

    /**
     * 화면에 표시될 카테고리 이름
     */
    @NonNull
    private final String displayName;

    /**
     * 카테고리 설명
     */
    private final String description;

    /**
     * 상위 카테고리 ID
     */
    private final Long parentCategoryId;

    /**
     * 시스템 카테고리 여부
     */
    private final boolean isSystem;

    /**
     * 기본 카테고리 생성을 위한 팩토리 메서드
     *
     * @param name        카테고리 이름
     * @param displayName 표시 이름
     * @param description 설명
     * @return 카테고리 생성 명령
     */
    public static CreateCategoryCommand create(
        @NonNull String name,
        @NonNull String displayName,
        String description) {
        return CreateCategoryCommand.builder()
            .name(name)
            .displayName(displayName)
            .description(description)
            .isSystem(false)
            .build();
    }

    /**
     * 시스템 카테고리 생성을 위한 팩토리 메서드
     *
     * @param name        카테고리 이름
     * @param displayName 표시 이름
     * @param description 설명
     * @return 시스템 카테고리 생성 명령
     */
    public static CreateCategoryCommand createSystemCategory(
        @NonNull String name,
        @NonNull String displayName,
        String description) {
        return CreateCategoryCommand.builder()
            .name(name)
            .displayName(displayName)
            .description(description)
            .isSystem(true)
            .build();
    }

    /**
     * 하위 카테고리 생성을 위한 팩토리 메서드
     *
     * @param name             카테고리 이름
     * @param displayName      표시 이름
     * @param description      설명
     * @param parentCategoryId 상위 카테고리 ID
     * @return 하위 카테고리 생성 명령
     */
    public static CreateCategoryCommand createSubCategory(
        @NonNull String name,
        @NonNull String displayName,
        String description,
        Long parentCategoryId) {
        return CreateCategoryCommand.builder()
            .name(name)
            .displayName(displayName)
            .description(description)
            .parentCategoryId(parentCategoryId)
            .isSystem(false)
            .build();
    }

    /**
     * 명령 객체에서 도메인 모델을 생성합니다.
     * 상위 카테고리가 있는 경우 null로 설정되며, 실제 상위 카테고리는 서비스에서 주입해야 합니다.
     *
     * @return 생성된 카테고리 도메인 모델
     */
    public Category toDomain() {
        return new Category(
            name,
            displayName,
            description,
            isSystem);
    }
}
