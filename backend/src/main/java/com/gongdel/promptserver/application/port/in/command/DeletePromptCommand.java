package com.gongdel.promptserver.application.port.in.command;

import com.gongdel.promptserver.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.UUID;

/**
 * 프롬프트 논리 삭제 요청을 처리하기 위한 커맨드 객체입니다.
 * 삭제할 프롬프트의 UUID와 삭제 요청 사용자를 포함합니다.
 */
@Getter
public class DeletePromptCommand {
    /**
     * 삭제할 프롬프트의 UUID
     */
    private final UUID uuid;

    /**
     * 삭제 요청을 수행하는 사용자
     */
    private final User currentUser;

    /**
     * DeletePromptCommand 객체를 생성합니다.
     *
     * @param uuid        삭제할 프롬프트의 UUID
     * @param currentUser 삭제 요청을 수행하는 사용자
     * @throws IllegalArgumentException uuid나 currentUser가 null인 경우
     */
    @Builder
    public DeletePromptCommand(UUID uuid, User currentUser) {
        Assert.notNull(uuid, "프롬프트 UUID는 null일 수 없습니다.");
        Assert.notNull(currentUser, "삭제 요청 사용자는 null일 수 없습니다.");
        this.uuid = uuid;
        this.currentUser = currentUser;
    }
}
