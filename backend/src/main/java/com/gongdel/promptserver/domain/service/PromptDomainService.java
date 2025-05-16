package com.gongdel.promptserver.domain.service;

import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PromptDomainService {

  public void validatePromptTemplate(PromptTemplate promptTemplate) {
    if (promptTemplate.getTitle() == null || promptTemplate.getTitle().trim().isEmpty()) {
      throw new IllegalArgumentException("Title cannot be empty");
    }
    if (promptTemplate.getContent() == null || promptTemplate.getContent().trim().isEmpty()) {
      throw new IllegalArgumentException("Content cannot be empty");
    }
  }

  public void validateUserCanEdit(PromptTemplate promptTemplate, User user) {
    if (!promptTemplate.getAuthor().getId().equals(user.getId())) {
      throw new IllegalStateException("User is not authorized to edit this prompt template");
    }
  }

  public void validateUserCanView(PromptTemplate promptTemplate, User user) {
    if (!promptTemplate.isPublic() && !promptTemplate.getAuthor().getId().equals(user.getId())) {
      throw new IllegalStateException("User is not authorized to view this prompt template");
    }
  }
}
