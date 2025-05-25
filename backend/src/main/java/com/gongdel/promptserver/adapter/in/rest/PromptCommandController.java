package com.gongdel.promptserver.adapter.in.rest;

import com.gongdel.promptserver.adapter.in.rest.request.CreatePromptRequest;
import com.gongdel.promptserver.adapter.in.rest.response.PromptResponse;
import com.gongdel.promptserver.application.port.in.PromptCommandUseCase;
import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;
import com.gongdel.promptserver.application.util.PromptSchemaConverter;
import com.gongdel.promptserver.domain.model.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.gongdel.promptserver.application.constant.DevelopmentConstants.*;

/**
 * 프롬프트 생성 등 명령성 작업을 처리하는 REST 컨트롤러입니다.
 */
@Slf4j
@Tag(name = "프롬프트 관리", description = "프롬프트 생성, 수정, 삭제 API")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptCommandController {

    private final PromptCommandUseCase registerPromptUseCase;

    /**
     * 새로운 프롬프트를 생성합니다.
     *
     * @param request 프롬프트 생성 요청 정보
     * @return 생성된 프롬프트 정보
     */
    @Operation(summary = "프롬프트 생성", description = "새로운 프롬프트를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "프롬프트 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public ResponseEntity<PromptResponse> createPrompt(@Valid @RequestBody CreatePromptRequest request) {
        log.info("Creating new prompt with title: [{}]", request.getTitle());
        RegisterPromptCommand command = buildRegisterPromptCommand(request);
        PromptTemplate promptTemplate = registerPromptUseCase.registerPrompt(command);
        log.info("Successfully created prompt with ID: [{}]", promptTemplate.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(PromptResponse.from(promptTemplate));
    }

    /**
     * 임시 사용자를 생성합니다. 실제 애플리케이션에서는 인증된 사용자 정보를 사용해야 합니다.
     *
     * @return 생성된 임시 사용자
     */
    private User createTemporaryUser() {
        return User.builder()
            .id(UUID.fromString(TEMP_USER_UUID))
            .name(TEMP_USER_NAME)
            .email(TEMP_USER_EMAIL)
            .role(UserRole.ROLE_USER)
            .build();
    }

    /**
     * 프롬프트 등록 요청으로부터 커맨드 객체를 생성합니다.
     *
     * @param request 프롬프트 생성 요청 정보
     * @return 프롬프트 등록 커맨드 객체
     */
    private RegisterPromptCommand buildRegisterPromptCommand(CreatePromptRequest request) {
        User author = (request.getCreatedBy() != null)
            ? toDomainUser(request.getCreatedBy())
            : createTemporaryUser();
        Map<String, Object> standardSchema = PromptSchemaConverter.convertToStandardSchema(
            request.getVariablesSchema());
        return RegisterPromptCommand.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .content(request.getContent())
            .createdBy(author)
            .tags(request.getTags() != null ? request.getTags() : convertTagIdsToTags(request.getTagIds()))
            .inputVariables(request.getInputVariables())
            .variablesSchema(standardSchema)
            .categoryId(request.getCategoryId())
            .visibility(parseVisibility(request.getVisibility(), request.isPublic(), author))
            .status(parseStatus(request.getStatus()))
            .build();
    }

    /**
     * CreatePromptRequest.UserDto를 도메인 User로 변환합니다.
     *
     * @param userDto 사용자 DTO
     * @return 도메인 User 객체
     */
    private User toDomainUser(CreatePromptRequest.UserDto userDto) {
        return User.builder()
            .id(userDto.getId() != null ? UUID.fromString(userDto.getId()) : UUID.randomUUID())
            .email(userDto.getEmail())
            .name(userDto.getName())
            .role(UserRole.ROLE_USER)
            .build();
    }

    /**
     * 가시성 파싱 (문자열, isPublic, 팀 여부 등)
     *
     * @param visibility 가시성 문자열
     * @param isPublic   공개 여부
     * @param author     작성자
     * @return Visibility 열거형
     */
    private Visibility parseVisibility(String visibility, boolean isPublic, User author) {
        if (visibility != null && !visibility.isBlank()) {
            try {
                return Visibility.valueOf(visibility.toUpperCase());
            } catch (Exception ignored) {
                // 무시하고 기본값 처리
            }
        }
        if (isPublic)
            return Visibility.PUBLIC;
        if (author.getTeam() != null)
            return Visibility.TEAM;
        return Visibility.PRIVATE;
    }

    /**
     * 상태 파싱 (문자열 → PromptStatus)
     *
     * @param status 상태 문자열
     * @return PromptStatus 열거형
     */
    private PromptStatus parseStatus(String status) {
        if (status != null && !status.isBlank()) {
            try {
                return PromptStatus.valueOf(status.toUpperCase());
            } catch (Exception ignored) {
                // 무시하고 기본값 처리
            }
        }
        return PromptStatus.DRAFT;
    }

    /**
     * 태그 ID 집합을 태그 문자열 집합으로 변환합니다.
     *
     * @param tagIds 태그 ID 집합
     * @return 태그 문자열 집합
     */
    private Set<String> convertTagIdsToTags(Set<UUID> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return Collections.emptySet();
        }
        return tagIds.stream()
            .map(UUID::toString)
            .collect(java.util.stream.Collectors.toSet());
    }
}
