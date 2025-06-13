package com.gongdel.promptserver.application.dto;

import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.user.UserId;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 프롬프트 삭제 결과를 반환하는 응답 DTO입니다.
 */
@Getter
@Builder
public class DeletePromptResponse {
    private final UUID uuid;
    private final String title;
    private final PromptStatus previousStatus;
    private final LocalDateTime deletedAt;
    private final UserId deletedBy;

    /**
     * 프롬프트 삭제 결과를 생성합니다.
     *
     * @param uuid           프롬프트 UUID
     * @param title          프롬프트 제목
     * @param previousStatus 삭제 전 상태
     * @param deletedAt      삭제 일시
     * @param deletedBy      삭제한 사용자 식별자
     * @return DeletePromptResponse 객체
     */
    public static DeletePromptResponse of(
        UUID uuid,
        String title,
        PromptStatus previousStatus,
        LocalDateTime deletedAt,
        UserId deletedBy) {

        Assert.notNull(uuid, "uuid must not be null");
        Assert.hasText(title, "title must not be blank");
        Assert.notNull(previousStatus, "previousStatus must not be null");
        Assert.notNull(deletedAt, "deletedAt must not be null");
        Assert.notNull(deletedBy, "deletedBy must not be null");

        return DeletePromptResponse.builder()
            .uuid(uuid)
            .title(title)
            .previousStatus(previousStatus)
            .deletedAt(deletedAt)
            .deletedBy(deletedBy)
            .build();
    }


}
