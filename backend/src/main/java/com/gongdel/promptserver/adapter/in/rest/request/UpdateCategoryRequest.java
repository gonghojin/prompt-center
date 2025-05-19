package com.gongdel.promptserver.adapter.in.rest.request;

import com.gongdel.promptserver.application.port.in.command.UpdateCategoryCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 카테고리 업데이트 요청 DTO
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCategoryRequest {

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
     * 요청 DTO를 커맨드 객체로 변환합니다.
     *
     * @param id 업데이트할 카테고리 ID
     * @return 생성된 커맨드 객체
     */
    public UpdateCategoryCommand toCommand(@NotNull Long id) {
        return UpdateCategoryCommand.builder()
                .id(id)
                .displayName(displayName)
                .description(description)
                .parentCategoryId(parentCategoryId)
                .build();
    }
}
