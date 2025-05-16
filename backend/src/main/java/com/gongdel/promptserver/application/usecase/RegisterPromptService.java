package com.gongdel.promptserver.application.usecase;

import com.gongdel.promptserver.application.port.in.RegisterPromptUseCase;
import com.gongdel.promptserver.application.port.in.command.RegisterPromptCommand;

import com.gongdel.promptserver.application.port.out.SavePromptPort;
import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.PromptValidationException;
import com.gongdel.promptserver.domain.model.Visibility;
import com.gongdel.promptserver.domain.service.PromptDomainService;
import java.util.HashSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 프롬프트 템플릿 등록을 위한 서비스 구현체입니다.
 * 헥사고널 아키텍처의 유스케이스 구현으로, 프롬프트 등록 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterPromptService implements RegisterPromptUseCase {

  private final SavePromptPort savePromptPort;
  private final PromptDomainService promptDomainService;

  /**
   * 새로운 프롬프트 템플릿을 등록합니다.
   * 입력된 커맨드 객체를 기반으로 프롬프트 템플릿을 생성하고 저장합니다.
   *
   * @param command 프롬프트 등록에 필요한 정보를 담은 커맨드 객체
   * @return 등록된 프롬프트 템플릿
   */
  @Override
  public PromptTemplate registerPrompt(RegisterPromptCommand command) {
    log.debug("Registering new prompt with title: {}", command.getTitle());

    try {
      PromptTemplate promptTemplate = createPromptTemplateFromCommand(command);

      // 도메인 모델 자체에서 생성 시 유효성 검증이 이루어져 별도의 검증 로직이 필요하지 않음
      log.debug("Creating prompt template with title: {}", command.getTitle());

      // 프롬프트 저장
      PromptTemplate savedPrompt = savePromptPort.savePrompt(promptTemplate);
      log.info("Prompt template successfully registered with ID: {}", savedPrompt.getId());

      return savedPrompt;
    } catch (PromptValidationException e) {
      log.error("Failed to create prompt template: {}", e.getMessage());
      throw new RuntimeException("프롬프트 템플릿 생성 중 오류가 발생했습니다: " + e.getMessage(), e);
    }
  }

  /**
   * 커맨드 객체로부터 프롬프트 템플릿 엔티티를 생성합니다.
   *
   * @param command 프롬프트 등록 커맨드
   * @return 생성된 프롬프트 템플릿 엔티티
   * @throws PromptValidationException 프롬프트 템플릿 유효성 검증에 실패한 경우
   */
  private PromptTemplate createPromptTemplateFromCommand(RegisterPromptCommand command)
      throws PromptValidationException {
    return PromptTemplate.builder()
        .id(UUID.randomUUID())
        .title(command.getTitle())
        .description(command.getDescription())
        .content(command.getContent())
        .author(command.getAuthor())
        .tags(new HashSet<>())
        .visibility(command.isPublic() ? Visibility.PUBLIC : Visibility.PRIVATE)
        .build();
  }
}
