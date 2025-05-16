package com.gongdel.promptserver.adapter.in.rest;

import com.gongdel.promptserver.adapter.in.rest.request.CreatePromptRequest;
import com.gongdel.promptserver.adapter.in.rest.response.PromptResponse;
import com.gongdel.promptserver.application.port.in.RegisterPromptUseCase;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.User;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/prompts")
@RequiredArgsConstructor
public class PromptController {

  private final RegisterPromptUseCase registerPromptUseCase;

  @PostMapping
  public ResponseEntity<PromptResponse> createPrompt(
      @RequestBody CreatePromptRequest request,
      @AuthenticationPrincipal User user
  ) {
    PromptTemplate promptTemplate = registerPromptUseCase.registerPrompt(
        request.getTitle(),
        request.getDescription(),
        request.getContent(),
        user,
        request.getTagIds(),
        request.isPublic()
    );

    return ResponseEntity.ok(PromptResponse.from(promptTemplate));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PromptResponse> getPrompt(@PathVariable UUID id) {
    // TODO: Implement getPrompt use case
    return ResponseEntity.ok().build();
  }

  @GetMapping
  public ResponseEntity<List<PromptResponse>> getAllPrompts() {
    // TODO: Implement getAllPrompts use case
    return ResponseEntity.ok().build();
  }

  @GetMapping("/search")
  public ResponseEntity<List<PromptResponse>> searchPrompts(@RequestParam String keyword) {
    // TODO: Implement searchPrompts use case
    return ResponseEntity.ok().build();
  }
}
