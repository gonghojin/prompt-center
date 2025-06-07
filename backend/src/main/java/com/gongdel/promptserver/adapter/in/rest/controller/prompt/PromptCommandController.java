package com.gongdel.promptserver.adapter.in.rest.controller.prompt;

import com.gongdel.promptserver.adapter.in.rest.request.prompt.CreatePromptRequest;
import com.gongdel.promptserver.adapter.in.rest.request.prompt.InputVariableDto;
import com.gongdel.promptserver.adapter.in.rest.response.prompt.CreatePromptResponse;
import com.gongdel.promptserver.application.dto.RegisterPromptResponse;
import com.gongdel.promptserver.application.port.in.PromptCommandUseCase;
import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;
import com.gongdel.promptserver.common.security.CurrentUserProvider;
import com.gongdel.promptserver.domain.exception.PromptValidationException;
import com.gongdel.promptserver.domain.model.InputVariable;
import com.gongdel.promptserver.domain.model.PromptStatus;
import com.gongdel.promptserver.domain.model.Visibility;
import com.gongdel.promptserver.domain.user.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 프롬프트 생성 등 명령성 작업을 처리하는 REST 컨트롤러입니다.
 */
@Slf4j
@Tag(name = "프롬프트 관리", description = "프롬프트 생성, 수정, 삭제 API")
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptCommandController {

    private final PromptCommandUseCase promptCommandUseCase;
    private final CurrentUserProvider currentUserProvider;

    /**
     * 새로운 프롬프트를 생성합니다.
     *
     * @param request 프롬프트 생성 요청 정보
     * @return 생성된 프롬프트 정보
     */
    @Operation(summary = "프롬프트 생성", description = "새로운 프롬프트를 생성합니다.")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "프롬프트 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "401", description = "인증되지 않은 요청")
    })
    @PostMapping
    public ResponseEntity<CreatePromptResponse> createPrompt(@Valid @RequestBody final CreatePromptRequest request) {
        log.info("Creating new prompt with title: [{}]", request.getTitle());
        validateCreatePromptRequest(request);

        final RegisterPromptCommand command = buildRegisterPromptCommand(request);
        final RegisterPromptResponse response = promptCommandUseCase.registerPrompt(command);
        log.info("Prompt created. ID: [{}]", response.getUuid());
        return ResponseEntity.status(HttpStatus.CREATED).body(CreatePromptResponse.from(response));
    }

    /**
     * 프롬프트 생성 요청의 필수 필드를 검증합니다.
     *
     * @param request 프롬프트 생성 요청 정보
     */
    private void validateCreatePromptRequest(final CreatePromptRequest request) {
        try {
            Assert.hasText(request.getTitle(), "Prompt title must not be empty");
            Assert.hasText(request.getContent(), "Prompt content must not be empty");
            Assert.hasText(request.getDescription(), "Prompt description must not be empty");
            Assert.notNull(request.getCategoryId(), "CategoryId must not be null");
            // 기타 필수 필드 검증 필요시 추가
        } catch (IllegalArgumentException e) {
            throw new PromptValidationException(e.getMessage(), e);
        }
    }

    /**
     * 프롬프트 등록 요청으로부터 커맨드 객체를 생성합니다.
     *
     * @param request 프롬프트 생성 요청 정보
     * @return 프롬프트 등록 커맨드 객체
     */
    private RegisterPromptCommand buildRegisterPromptCommand(final CreatePromptRequest request) {
        final User author = currentUserProvider.getCurrentUser();
        final List<InputVariable> inputVariables = mapInputVariables(request.getInputVariables());

        return RegisterPromptCommand.builder()
            .title(request.getTitle())
            .description(request.getDescription())
            .content(request.getContent())
            .createdBy(author)
            .tags(request.getTags())
            .inputVariables(inputVariables)
            .categoryId(request.getCategoryId())
            .visibility(parseVisibility(request.getVisibility(), author))
            .status(parseStatus(request.getStatus()))
            .build();
    }

    /**
     * 입력 변수 DTO 리스트를 도메인 객체 리스트로 변환합니다.
     *
     * @param inputVariableDtos 입력 변수 DTO 리스트
     * @return 도메인 InputVariable 리스트
     */
    private List<InputVariable> mapInputVariables(final List<InputVariableDto> inputVariableDtos) {
        if (inputVariableDtos == null || inputVariableDtos.isEmpty()) {
            return List.of();
        }
        return inputVariableDtos.stream()
            .map(InputVariableDto::toDomain)
            .collect(Collectors.toUnmodifiableList());
    }

    /**
     * 가시성 파싱 (문자열, isPublic, 팀 여부 등)
     *
     * @param visibility 가시성 문자열
     * @param author     작성자
     * @return Visibility 열거형
     */
    private Visibility parseVisibility(final String visibility, final User author) {
        if (visibility != null && !visibility.isBlank()) {
            try {
                return Visibility.valueOf(visibility.toUpperCase());
            } catch (Exception e) {
                log.warn("Invalid visibility value: {}. Defaulting to PRIVATE or TEAM.", visibility);
            }
        }
        return (author.getTeam() != null) ? Visibility.TEAM : Visibility.PRIVATE;
    }

    /**
     * 상태 파싱 (문자열 → PromptStatus)
     *
     * @param status 상태 문자열
     * @return PromptStatus 열거형
     */
    private PromptStatus parseStatus(final String status) {
        if (status != null && !status.isBlank()) {
            try {
                return PromptStatus.valueOf(status.toUpperCase());
            } catch (Exception e) {
                log.warn("Invalid status value: {}. Defaulting to DRAFT.", status);
            }
        }
        return PromptStatus.DRAFT;
    }
}
