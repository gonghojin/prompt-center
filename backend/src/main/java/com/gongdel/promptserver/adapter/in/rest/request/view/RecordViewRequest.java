package com.gongdel.promptserver.adapter.in.rest.request.view;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 프롬프트 조회수 기록 요청 DTO입니다.
 * 비로그인 사용자를 위한 익명 ID를 지원합니다.
 */
@Getter
@NoArgsConstructor
@Schema(description = "프롬프트 조회수 기록 요청 DTO")
public class RecordViewRequest {

    /**
     * 익명 사용자 ID (쿠키 기반 식별용, 선택사항)
     */
    @Schema(description = "익명 사용자 ID (비로그인 사용자 식별용)", example = "anonymous_12345", required = false)
    private String anonymousId;

    @Builder
    public RecordViewRequest(String anonymousId) {
        this.anonymousId = anonymousId;
    }

    /**
     * 정적 팩토리 메서드 - 익명 사용자용
     */
    public static RecordViewRequest forAnonymous(String anonymousId) {
        return RecordViewRequest.builder()
            .anonymousId(anonymousId)
            .build();
    }

    /**
     * 정적 팩토리 메서드 - 로그인 사용자용 (익명 ID 불필요)
     */
    public static RecordViewRequest forUser() {
        return RecordViewRequest.builder().build();
    }
}
