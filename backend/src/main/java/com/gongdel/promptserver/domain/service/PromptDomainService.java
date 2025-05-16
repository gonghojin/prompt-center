package com.gongdel.promptserver.domain.service;

import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 프롬프트 템플릿 도메인 서비스
 * 프롬프트 권한 확인 로직을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptDomainService {

  // 예외 메시지 상수화
  private static final String ERROR_UNAUTHORIZED_EDIT = "User is not authorized to edit this prompt template";
  private static final String ERROR_UNAUTHORIZED_VIEW = "User is not authorized to view this prompt template";

  /**
   * 사용자가 프롬프트 템플릿을 편집할 권한이 있는지 검증합니다.
   *
   * @param promptTemplate 검증할 프롬프트 템플릿
   * @param user           권한을 확인할 사용자
   * @throws IllegalStateException 사용자가 프롬프트 템플릿을 편집할 권한이 없는 경우
   */
  public void validateUserCanEdit(PromptTemplate promptTemplate, User user) {
    log.debug("Validating edit permission for user: {} on prompt: {}", user.getEmail(), promptTemplate.getId());

    if (!isAuthor(promptTemplate, user)) {
      log.warn("Unauthorized edit attempt by user: {} for prompt: {}", user.getEmail(), promptTemplate.getId());
      throw new IllegalStateException(ERROR_UNAUTHORIZED_EDIT);
    }
  }

  /**
   * 사용자가 프롬프트 템플릿을 조회할 권한이 있는지 검증합니다.
   *
   * @param promptTemplate 검증할 프롬프트 템플릿
   * @param user           권한을 확인할 사용자
   * @throws IllegalStateException 사용자가 프롬프트 템플릿을 조회할 권한이 없는 경우
   */
  public void validateUserCanView(PromptTemplate promptTemplate, User user) {
    log.debug("Validating view permission for user: {} on prompt: {}", user.getEmail(), promptTemplate.getId());

    if (!promptTemplate.isPublic() && !isAuthor(promptTemplate, user)) {
      log.warn("Unauthorized view attempt by user: {} for prompt: {}", user.getEmail(), promptTemplate.getId());
      throw new IllegalStateException(ERROR_UNAUTHORIZED_VIEW);
    }
  }

  /**
   * 사용자가 프롬프트 템플릿의 작성자인지 확인합니다.
   *
   * @param promptTemplate 확인할 프롬프트 템플릿
   * @param user           확인할 사용자
   * @return 사용자가 작성자인 경우 true, 아닌 경우 false
   */
  private boolean isAuthor(PromptTemplate promptTemplate, User user) {
    return promptTemplate.getAuthor().getId().equals(user.getId());
  }
}
