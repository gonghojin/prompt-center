package com.gongdel.promptserver.adapter.in.rest;

import com.gongdel.promptserver.adapter.in.rest.request.CreatePromptRequest;
import com.gongdel.promptserver.adapter.in.rest.response.PromptResponse;
import com.gongdel.promptserver.application.port.in.GetPromptsUseCase;
import com.gongdel.promptserver.application.port.in.RegisterPromptUseCase;
import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 프롬프트 리소스에 대한 REST API를 제공하는 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptController {

  private final RegisterPromptUseCase registerPromptUseCase;
  private final GetPromptsUseCase getPromptsUseCase;

  /**
   * 새로운 프롬프트를 생성합니다.
   *
   * @param request 프롬프트 생성 요청 정보
   * @return 생성된 프롬프트 정보
   */
  @PostMapping
  public ResponseEntity<PromptResponse> createPrompt(@RequestBody CreatePromptRequest request) {
    log.info("Creating new prompt with title: [{}]", request.getTitle());

    // 시큐리티가 없으므로 임시 사용자 생성
    User tempUser = createTemporaryUser();

    RegisterPromptCommand command = buildRegisterPromptCommand(request, tempUser);
    PromptTemplate promptTemplate = registerPromptUseCase.registerPrompt(command);

    log.info("Successfully created prompt with ID: [{}]", promptTemplate.getId());
    return ResponseEntity.ok(PromptResponse.from(promptTemplate));
  }

  /**
   * 임시 사용자를 생성합니다.
   * 실제 애플리케이션에서는 인증된 사용자 정보를 사용해야 합니다.
   *
   * @return 생성된 임시 사용자
   */
  private User createTemporaryUser() {
    return User.builder()
        .id(UUID.randomUUID())
        .name("임시 사용자")
        .email("temp@example.com")
        .build();
  }

  /**
   * 프롬프트 등록 요청으로부터 커맨드 객체를 생성합니다.
   *
   * @param request 프롬프트 생성 요청 정보
   * @param author  프롬프트 작성자
   * @return 프롬프트 등록 커맨드 객체
   */
  private RegisterPromptCommand buildRegisterPromptCommand(CreatePromptRequest request, User author) {
    return RegisterPromptCommand.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .content(request.getContent())
        .author(author)
        .tagIds(request.getTagIds())
        .isPublic(request.isPublic())
        .build();
  }

  /**
   * ID로 특정 프롬프트를 조회합니다.
   *
   * @param id 프롬프트 ID
   * @return 프롬프트 정보 또는 404 응답
   */
  @GetMapping("/{id}")
  public ResponseEntity<PromptResponse> getPrompt(@PathVariable UUID id) {
    log.info("Retrieving prompt with id: {}", id);
    Optional<PromptTemplate> prompt = getPromptsUseCase.getPromptById(id);

    return prompt
        .map(p -> ResponseEntity.ok(PromptResponse.from(p)))
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * 모든 프롬프트 목록을 조회합니다.
   *
   * @return 프롬프트 목록
   */
  @GetMapping
  public ResponseEntity<List<PromptResponse>> getAllPrompts() {
    log.info("Retrieving all prompts");
    List<PromptTemplate> prompts = getPromptsUseCase.getAllPrompts();
    List<PromptResponse> response = prompts.stream()
        .map(PromptResponse::from)
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  /**
   * 공개된 프롬프트 목록을 조회합니다.
   *
   * @return 공개 프롬프트 목록
   */
  @GetMapping("/public")
  public ResponseEntity<List<PromptResponse>> getPublicPrompts() {
    log.info("Retrieving public prompts");
    List<PromptTemplate> prompts = getPromptsUseCase.getPublicPrompts();
    List<PromptResponse> response = prompts.stream()
        .map(PromptResponse::from)
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  /**
   * 특정 작성자의 프롬프트 목록을 조회합니다.
   *
   * @param authorId 작성자 ID
   * @return 작성자의 프롬프트 목록
   */
  @GetMapping("/author/{authorId}")
  public ResponseEntity<List<PromptResponse>> getPromptsByAuthor(@PathVariable UUID authorId) {
    log.info("Retrieving prompts by author: {}", authorId);
    List<PromptTemplate> prompts = getPromptsUseCase.getPromptsByAuthor(authorId);
    List<PromptResponse> response = prompts.stream()
        .map(PromptResponse::from)
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }

  /**
   * 키워드로 프롬프트를 검색합니다.
   *
   * @param keyword 검색 키워드
   * @return 검색 결과 프롬프트 목록
   */
  @GetMapping("/search")
  public ResponseEntity<List<PromptResponse>> searchPrompts(@RequestParam String keyword) {
    log.info("Searching prompts with keyword: {}", keyword);
    List<PromptTemplate> prompts = getPromptsUseCase.searchPrompts(keyword);
    List<PromptResponse> response = prompts.stream()
        .map(PromptResponse::from)
        .collect(Collectors.toList());

    return ResponseEntity.ok(response);
  }
}
