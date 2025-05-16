package com.gongdel.promptserver.adapter.in.rest.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.gongdel.promptserver.domain.model.PromptTemplate;
import com.gongdel.promptserver.domain.model.PromptValidationException;
import com.gongdel.promptserver.domain.model.Tag;
import com.gongdel.promptserver.domain.model.User;
import com.gongdel.promptserver.domain.model.UserRole;
import com.gongdel.promptserver.domain.model.Visibility;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * PromptResponse DTO 클래스에 대한 단위 테스트
 */
class PromptResponseTest {

    @Test
    @DisplayName("PromptTemplate 도메인 객체로부터 PromptResponse DTO를 생성할 수 있다")
    void fromShouldCreateDtoFromDomain() throws PromptValidationException {
        // given
        UUID promptId = UUID.randomUUID();
        String title = "테스트 프롬프트";
        String description = "테스트 설명";
        String content = "테스트 내용";
        boolean isPublic = true;

        // 작성자 생성
        User author = User.builder()
                .id(UUID.randomUUID())
                .name("테스트 작성자")
                .email("author@example.com")
                .password("password")
                .role(UserRole.ROLE_USER)
                .build();

        // 태그 생성
        Set<Tag> tags = new HashSet<>();
        tags.add(Tag.builder().id(UUID.randomUUID()).name("태그1").build());
        tags.add(Tag.builder().id(UUID.randomUUID()).name("태그2").build());

        // 프롬프트 템플릿 생성
        PromptTemplate promptTemplate = new PromptTemplate(
                promptId, title, description, content, author, tags,
                isPublic ? Visibility.PUBLIC : Visibility.PRIVATE);

        // 조회수 및 좋아요 설정
        int viewCount = 10;
        int favoriteCount = 5;
        for (int i = 0; i < viewCount; i++) {
            promptTemplate.getStats().incrementViewCount();
        }
        for (int i = 0; i < favoriteCount; i++) {
            promptTemplate.getStats().incrementFavoriteCount();
        }

        // when
        PromptResponse promptResponse = PromptResponse.from(promptTemplate);

        // then
        assertThat(promptResponse).isNotNull();
        assertThat(promptResponse.getId()).isEqualTo(promptId);
        assertThat(promptResponse.getTitle()).isEqualTo(title);
        assertThat(promptResponse.getDescription()).isEqualTo(description);
        assertThat(promptResponse.getContent()).isEqualTo(content);
        assertThat(promptResponse.isPublic()).isEqualTo(isPublic);
        assertThat(promptResponse.getCreatedAt()).isNotNull();
        assertThat(promptResponse.getUpdatedAt()).isNotNull();
        assertThat(promptResponse.getViewCount()).isEqualTo(viewCount);
        assertThat(promptResponse.getFavoriteCount()).isEqualTo(favoriteCount);

        // 작성자 검증
        assertThat(promptResponse.getAuthor()).isNotNull();
        assertThat(promptResponse.getAuthor().getId()).isEqualTo(author.getId());
        assertThat(promptResponse.getAuthor().getName()).isEqualTo(author.getName());
        assertThat(promptResponse.getAuthor().getEmail()).isEqualTo(author.getEmail());

        // 태그 검증
        assertThat(promptResponse.getTags()).isNotNull();
        assertThat(promptResponse.getTags()).hasSize(2);
        assertThat(promptResponse.getTags())
                .extracting("name")
                .containsExactlyInAnyOrder("태그1", "태그2");
    }

    @Test
    @DisplayName("PromptResponse 빌더를 사용하여 DTO 객체를 생성할 수 있다")
    void builderShouldCreateDto() {
        // given
        UUID id = UUID.randomUUID();
        String title = "빌더 테스트";
        String description = "빌더 설명";
        String content = "빌더 내용";
        UserResponse author = UserResponse.builder()
                .id(UUID.randomUUID())
                .name("빌더 작성자")
                .email("builder@example.com")
                .build();
        Set<TagResponse> tags = new HashSet<>();
        tags.add(TagResponse.builder().id(UUID.randomUUID()).name("빌더태그").build());
        boolean isPublic = true;
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        LocalDateTime updatedAt = LocalDateTime.now();
        int viewCount = 5;
        int favoriteCount = 3;

        // when
        PromptResponse promptResponse = PromptResponse.builder()
                .id(id)
                .title(title)
                .description(description)
                .content(content)
                .author(author)
                .tags(tags)
                .isPublic(isPublic)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .viewCount(viewCount)
                .favoriteCount(favoriteCount)
                .build();

        // then
        assertThat(promptResponse).isNotNull();
        assertThat(promptResponse.getId()).isEqualTo(id);
        assertThat(promptResponse.getTitle()).isEqualTo(title);
        assertThat(promptResponse.getDescription()).isEqualTo(description);
        assertThat(promptResponse.getContent()).isEqualTo(content);
        assertThat(promptResponse.getAuthor()).isEqualTo(author);
        assertThat(promptResponse.getTags()).isEqualTo(tags);
        assertThat(promptResponse.isPublic()).isEqualTo(isPublic);
        assertThat(promptResponse.getCreatedAt()).isEqualTo(createdAt);
        assertThat(promptResponse.getUpdatedAt()).isEqualTo(updatedAt);
        assertThat(promptResponse.getViewCount()).isEqualTo(viewCount);
        assertThat(promptResponse.getFavoriteCount()).isEqualTo(favoriteCount);
    }
}
