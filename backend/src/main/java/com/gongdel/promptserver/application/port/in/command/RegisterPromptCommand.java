package com.gongdel.promptserver.application.port.in.command;

import com.gongdel.promptserver.domain.model.User;
import java.util.Set;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/**
 * 프롬프트 등록 커맨드 객체입니다.
 * 프롬프트 템플릿 생성에 필요한 모든 정보를 캡슐화합니다.
 */
@Getter
@Builder
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
     * 프롬프트 템플릿의 내용
     */
    @NonNull
    private final String content;

    /**
     * 프롬프트 템플릿의 작성자
     */
    @NonNull
    private final User author;

    /**
     * 프롬프트 템플릿에 연결된 태그 ID 목록
     */
    private final Set<UUID> tagIds;

    /**
     * 프롬프트 템플릿의 공개 여부
     */
    private final boolean isPublic;
}
