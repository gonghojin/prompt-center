package com.gongdel.promptserver.adapter.in.rest.request;

import com.gongdel.promptserver.application.port.in.command.CreateCategoryCommand;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 카테고리 생성 요청 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCategoryRequest {

    /**
     * 카테고리 고유 이름
     */
    @NotBlank(message = "카테고리 이름은 필수입니다")
    private String name;

    /**
     * 화면에 표시될 카테고리 이름
     */
    @NotBlank(message = "카테고리 표시 이름은 필수입니다")
    private String displayName;

    /**
     * 카테고리 설명
     */
    private String description;

    /**
     * 상위 카테고리 ID
     */
    private Long parentCategoryId;

    /**
     * 시스템 카테고리 여부
     */
    private boolean isSystem;

    /**
     * 요청 DTO를 커맨드 객체로 변환합니다.
     *
     * @return 생성된 커맨드 객체
     */
    public CreateCategoryCommand toCommand() {
        return CreateCategoryCommand.builder()
                .name(name)
                .displayName(displayName)
                .description(description)
                .parentCategoryId(parentCategoryId)
                .isSystem(isSystem)
                .build();
    }
}
