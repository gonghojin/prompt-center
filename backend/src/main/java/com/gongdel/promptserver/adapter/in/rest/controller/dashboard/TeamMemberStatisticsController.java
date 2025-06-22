package com.gongdel.promptserver.adapter.in.rest.controller.dashboard;

import com.gongdel.promptserver.adapter.in.rest.response.dashboard.TeamMemberStatisticsResponse;
import com.gongdel.promptserver.application.port.in.TeamMemberStatisticsQueryUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 대시보드에서 팀멤버 통계를 제공하는 컨트롤러입니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/dashboard/team-member-statistics")
@RequiredArgsConstructor
@Tag(name = "대시보드 팀멤버 통계", description = "대시보드용 팀멤버 통계 API")
public class TeamMemberStatisticsController {

    private final TeamMemberStatisticsQueryUseCase teamMemberStatisticsQueryUseCase;

    /**
     * 전체 활성 팀멤버 수를 조회합니다.
     * 대시보드에서 "85 팀멤버" 형태로 표시할 데이터를 제공합니다.
     *
     * @return 팀멤버 통계 응답
     */
    @GetMapping
    @Operation(summary = "전체 팀멤버 통계 조회", description = "시스템 전체의 활성 팀멤버 개수를 조회합니다. 대시보드 통계 카드에서 사용됩니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "팀멤버 통계 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 오류 - 통계 데이터 조회 실패")
    })
    public ResponseEntity<TeamMemberStatisticsResponse> getTeamMemberStatistics() {
        log.info("Request team member statistics for dashboard");

        long totalCount = teamMemberStatisticsQueryUseCase.getTotalActiveMemberCount();

        TeamMemberStatisticsResponse response = TeamMemberStatisticsResponse.from(totalCount);

        log.info("Team member statistics loaded: totalCount={}", totalCount);

        return ResponseEntity.ok(response);
    }
}
