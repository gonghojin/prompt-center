package com.gongdel.promptserver.adapter.out.persistence.repository;

import lombok.Value;

@Value(staticConstructor = "of")
public class PromptLikeCountProjection {
    Long promptTemplateId;
    Long likeCount;
}
