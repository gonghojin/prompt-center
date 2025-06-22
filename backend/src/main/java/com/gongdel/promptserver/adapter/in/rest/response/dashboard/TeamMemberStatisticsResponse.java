package com.gongdel.promptserver.adapter.in.rest.response.dashboard;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 팀멤버 통계 대시보드 응답 DTO입니다.
 */
@Getter
@Builder
@Schema(description = "팀멤버 통계 대시보드 응답 DTO")
public class TeamMemberStatisticsResponse {

    @Schema(description = "전체 팀멤버 개수", example = "85")
    private final long totalMemberCount;

    /**
     * 총 팀멤버 개수로부터 응답 DTO를 생성합니다.
     *
     * @param totalCount 총 팀멤버 개수
     * @return 팀멤버 통계 응답 DTO
     */
    public static TeamMemberStatisticsResponse from(long totalCount) {
        return TeamMemberStatisticsResponse.builder()
            .totalMemberCount(totalCount)
            .build();
    }
}
