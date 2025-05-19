package com.gongdel.promptserver.application.port.in.command;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * 카테고리 업데이트를 위한 명령 객체입니다.
 */
@Getter
@Builder
public class UpdateCategoryCommand {

    /**
     * 카테고리 ID
     */
    @NonNull
    private final Long id;

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
     * 기본 업데이트 명령 생성 메서드
     *
     * @param id          카테고리 ID
     * @param displayName 표시 이름
     * @param description 설명
     * @return 업데이트 명령
     */
    public static UpdateCategoryCommand create(
            @NonNull Long id,
            @NonNull String displayName,
            String description) {
        return UpdateCategoryCommand.builder()
                .id(id)
                .displayName(displayName)
                .description(description)
                .build();
    }

    /**
     * 상위 카테고리 변경을 포함한 업데이트 명령 생성 메서드
     *
     * @param id               카테고리 ID
     * @param displayName      표시 이름
     * @param description      설명
     * @param parentCategoryId 상위 카테고리 ID
     * @return 업데이트 명령
     */
    public static UpdateCategoryCommand createWithParent(
            @NonNull Long id,
            @NonNull String displayName,
            String description,
            Long parentCategoryId) {
        return UpdateCategoryCommand.builder()
                .id(id)
                .displayName(displayName)
                .description(description)
                .parentCategoryId(parentCategoryId)
                .build();
    }
}
