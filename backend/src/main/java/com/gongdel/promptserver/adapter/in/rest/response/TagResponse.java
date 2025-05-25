package com.gongdel.promptserver.adapter.in.rest.response;

import com.gongdel.promptserver.domain.model.Tag;
import lombok.Builder;
import lombok.Getter;

/**
 * 태그 정보를 클라이언트에 응답하기 위한 DTO 클래스입니다.
 * 태그의 식별자와 이름 정보를 포함합니다.
 */
@Getter
@Builder
public class TagResponse {

    private final Long id;
    private final String name;

    /**
     * 태그 도메인 모델로부터 응답 DTO를 생성합니다.
     *
     * @param tag 변환할 태그 도메인 객체
     * @return 태그 응답 DTO
     */
    public static TagResponse from(Tag tag) {
        return TagResponse.builder()
            .id(tag.getId())
            .name(tag.getName())
            .build();
    }
}
