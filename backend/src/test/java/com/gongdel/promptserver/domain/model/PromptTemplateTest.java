package com.gongdel.promptserver.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * PromptTemplate 도메인 모델 테스트
 */
class PromptTemplateTest {

    private User author;
    private Tag tag1;
    private Tag tag2;

    @BeforeEach
    void setUp() {
        author = User.builder()
                .id(UUID.randomUUID())
                .email("test@example.com")
                .name("테스트 사용자")
                .password("password1234")
                .build();

        tag1 = Tag.builder()
                .id(UUID.randomUUID())
                .name("태그1")
                .build();

        tag2 = Tag.builder()
                .id(UUID.randomUUID())
                .name("태그2")
                .build();
    }

    @Nested
    @DisplayName("프롬프트 템플릿 생성")
    class Creation {
        @Test
        @DisplayName("Builder를 사용하여 PromptTemplate 객체를 생성할 수 있다")
        void createPromptTemplateWithBuilder() throws PromptValidationException {
            // given
            UUID id = UUID.randomUUID();
            String title = "테스트 프롬프트";
            String description = "테스트 설명";
            String content = "테스트 내용";
            Set<Tag> tags = new HashSet<>();
            tags.add(tag1);
            tags.add(tag2);
            Visibility visibility = Visibility.PUBLIC;

            // when
            PromptTemplate promptTemplate = PromptTemplate.builder()
                    .id(id)
                    .title(title)
                    .description(description)
                    .content(content)
                    .author(author)
                    .tags(tags)
                    .visibility(visibility)
                    .build();

            // then
            assertThat(promptTemplate.getId()).isEqualTo(id);
            assertThat(promptTemplate.getTitle()).isEqualTo(title);
            assertThat(promptTemplate.getDescription()).isEqualTo(description);
            assertThat(promptTemplate.getContent()).isEqualTo(content);
            assertThat(promptTemplate.getAuthor()).isEqualTo(author);
            assertThat(promptTemplate.getTags()).containsExactlyInAnyOrder(tag1, tag2);
            assertThat(promptTemplate.getVisibility()).isEqualTo(visibility);
            assertThat(promptTemplate.getCreatedAt()).isNotNull();
            assertThat(promptTemplate.getUpdatedAt()).isNotNull();
        }

        @Test
        @DisplayName("id가 null이면 UUID가 자동 생성된다")
        void createWithNullId() throws PromptValidationException {
            // when
            PromptTemplate promptTemplate = PromptTemplate.builder()
                    .title("테스트 프롬프트")
                    .description("테스트 설명")
                    .content("테스트 내용")
                    .author(author)
                    .build();

            // then
            assertThat(promptTemplate.getId()).isNotNull();
        }

        @Test
        @DisplayName("visibility가 null이면 기본값으로 PRIVATE가 설정된다")
        void createWithNullVisibility() throws PromptValidationException {
            // when
            PromptTemplate promptTemplate = PromptTemplate.builder()
                    .title("테스트 프롬프트")
                    .description("테스트 설명")
                    .content("테스트 내용")
                    .author(author)
                    .visibility(null)
                    .build();

            // then
            assertThat(promptTemplate.getVisibility()).isEqualTo(Visibility.PRIVATE);
        }
    }

    @Nested
    @DisplayName("프롬프트 템플릿 유효성 검증")
    class Validation {
        @Test
        @DisplayName("제목이 null이거나 비어있으면 예외가 발생한다")
        void validateEmptyTitle() {
            // then
            assertThatThrownBy(() -> PromptTemplate.builder()
                    .title(null)
                    .description("테스트 설명")
                    .content("테스트 내용")
                    .author(author)
                    .build())
                    .isInstanceOf(PromptValidationException.class)
                    .hasMessageContaining("Title cannot be empty");

            assertThatThrownBy(() -> PromptTemplate.builder()
                    .title("")
                    .description("테스트 설명")
                    .content("테스트 내용")
                    .author(author)
                    .build())
                    .isInstanceOf(PromptValidationException.class)
                    .hasMessageContaining("Title cannot be empty");
        }

        @Test
        @DisplayName("내용이 null이거나 비어있으면 예외가 발생한다")
        void validateEmptyContent() {
            // then
            assertThatThrownBy(() -> PromptTemplate.builder()
                    .title("테스트 프롬프트")
                    .description("테스트 설명")
                    .content(null)
                    .author(author)
                    .build())
                    .isInstanceOf(PromptValidationException.class)
                    .hasMessageContaining("Content cannot be empty");

            assertThatThrownBy(() -> PromptTemplate.builder()
                    .title("테스트 프롬프트")
                    .description("테스트 설명")
                    .content("")
                    .author(author)
                    .build())
                    .isInstanceOf(PromptValidationException.class)
                    .hasMessageContaining("Content cannot be empty");
        }

        @Test
        @DisplayName("작성자가 null이면 예외가 발생한다")
        void validateNullAuthor() {
            // then
            assertThatThrownBy(() -> PromptTemplate.builder()
                    .title("테스트 프롬프트")
                    .description("테스트 설명")
                    .content("테스트 내용")
                    .author(null)
                    .build())
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Author cannot be null");
        }

        @Test
        @DisplayName("제목, 내용, 설명의 길이가 최대 길이를 초과하면 예외가 발생한다")
        void validateMaxLength() {
            // given
            String longTitle = "a".repeat(PromptTemplate.MAX_TITLE_LENGTH + 1);
            String longContent = "a".repeat(PromptTemplate.MAX_CONTENT_LENGTH + 1);
            String longDescription = "a".repeat(PromptTemplate.MAX_DESCRIPTION_LENGTH + 1);

            // then
            assertThatThrownBy(() -> PromptTemplate.builder()
                    .title(longTitle)
                    .description("테스트 설명")
                    .content("테스트 내용")
                    .author(author)
                    .build())
                    .isInstanceOf(PromptValidationException.class)
                    .hasMessageContaining("Title must be less than " + PromptTemplate.MAX_TITLE_LENGTH + " characters");

            assertThatThrownBy(() -> PromptTemplate.builder()
                    .title("테스트 프롬프트")
                    .description("테스트 설명")
                    .content(longContent)
                    .author(author)
                    .build())
                    .isInstanceOf(PromptValidationException.class)
                    .hasMessageContaining(
                            "Content must be less than " + PromptTemplate.MAX_CONTENT_LENGTH + " characters");

            assertThatThrownBy(() -> PromptTemplate.builder()
                    .title("테스트 프롬프트")
                    .description(longDescription)
                    .content("테스트 내용")
                    .author(author)
                    .build())
                    .isInstanceOf(PromptValidationException.class)
                    .hasMessageContaining(
                            "Description must be less than " + PromptTemplate.MAX_DESCRIPTION_LENGTH + " characters");
        }
    }

    @Nested
    @DisplayName("프롬프트 템플릿 업데이트")
    class Update {
        @Test
        @DisplayName("프롬프트 템플릿을 업데이트할 수 있다")
        void updatePromptTemplate() throws PromptValidationException {
            // given
            PromptTemplate promptTemplate = PromptTemplate.builder()
                    .title("원래 제목")
                    .description("원래 설명")
                    .content("원래 내용")
                    .author(author)
                    .visibility(Visibility.PRIVATE)
                    .build();

            LocalDateTime originalUpdatedAt = promptTemplate.getUpdatedAt();

            Set<Tag> newTags = new HashSet<>();
            newTags.add(tag1);
            newTags.add(tag2);

            // 약간의 시간 지연을 위해 잠시 대기
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // 무시
            }

            // when
            String newTitle = "새로운 제목";
            String newDescription = "새로운 설명";
            String newContent = "새로운 내용";
            Visibility newVisibility = Visibility.PUBLIC;

            promptTemplate.update(newTitle, newDescription, newContent, newTags, newVisibility);

            // then
            assertThat(promptTemplate.getTitle()).isEqualTo(newTitle);
            assertThat(promptTemplate.getDescription()).isEqualTo(newDescription);
            assertThat(promptTemplate.getContent()).isEqualTo(newContent);
            assertThat(promptTemplate.getTags()).containsExactlyInAnyOrder(tag1, tag2);
            assertThat(promptTemplate.getVisibility()).isEqualTo(newVisibility);
            assertThat(promptTemplate.getUpdatedAt()).isAfter(originalUpdatedAt);
        }
    }

    @Nested
    @DisplayName("태그 관리")
    class TagManagement {
        @Test
        @DisplayName("태그를 추가하고 제거할 수 있다")
        void addAndRemoveTag() throws PromptValidationException {
            // given
            PromptTemplate promptTemplate = PromptTemplate.builder()
                    .title("테스트 프롬프트")
                    .description("테스트 설명")
                    .content("테스트 내용")
                    .author(author)
                    .build();

            // when - then
            assertThat(promptTemplate.getTags()).isEmpty();

            // 태그 추가
            boolean added = promptTemplate.addTag(tag1);
            assertThat(added).isTrue();
            assertThat(promptTemplate.getTags()).contains(tag1);

            // 같은 태그는 중복 추가 불가
            added = promptTemplate.addTag(tag1);
            assertThat(added).isFalse();
            assertThat(promptTemplate.getTags()).hasSize(1);

            // 다른 태그 추가
            added = promptTemplate.addTag(tag2);
            assertThat(added).isTrue();
            assertThat(promptTemplate.getTags()).containsExactlyInAnyOrder(tag1, tag2);

            // null 태그 추가 불가
            added = promptTemplate.addTag(null);
            assertThat(added).isFalse();
            assertThat(promptTemplate.getTags()).hasSize(2);

            // 태그 제거
            boolean removed = promptTemplate.removeTag(tag1);
            assertThat(removed).isTrue();
            assertThat(promptTemplate.getTags()).containsExactly(tag2);

            // 존재하지 않는 태그 제거
            removed = promptTemplate.removeTag(tag1);
            assertThat(removed).isFalse();
        }
    }

    @Nested
    @DisplayName("가시성 관리")
    class VisibilityManagement {
        @Test
        @DisplayName("isPublic 메서드는 visibility가 PUBLIC인지 여부를 반환한다")
        void isPublic() throws PromptValidationException {
            // given
            PromptTemplate publicPrompt = PromptTemplate.builder()
                    .title("공개 프롬프트")
                    .description("테스트 설명")
                    .content("테스트 내용")
                    .author(author)
                    .visibility(Visibility.PUBLIC)
                    .build();

            PromptTemplate privatePrompt = PromptTemplate.builder()
                    .title("비공개 프롬프트")
                    .description("테스트 설명")
                    .content("테스트 내용")
                    .author(author)
                    .visibility(Visibility.PRIVATE)
                    .build();

            // then
            assertThat(publicPrompt.isPublic()).isTrue();
            assertThat(privatePrompt.isPublic()).isFalse();
        }
    }

    @Nested
    @DisplayName("통계 관리")
    class StatsManagement {
        @Test
        @DisplayName("조회수와 좋아요 수를 증가시키고 감소시킬 수 있다")
        void statsManagement() throws PromptValidationException {
            // given
            PromptTemplate promptTemplate = PromptTemplate.builder()
                    .title("테스트 프롬프트")
                    .description("테스트 설명")
                    .content("테스트 내용")
                    .author(author)
                    .build();

            // 초기값 검증
            assertThat(promptTemplate.getViewCount()).isZero();
            assertThat(promptTemplate.getFavoriteCount()).isZero();

            // 조회수 증가
            promptTemplate.incrementViewCount();
            assertThat(promptTemplate.getViewCount()).isEqualTo(1);

            promptTemplate.incrementViewCount();
            assertThat(promptTemplate.getViewCount()).isEqualTo(2);

            // 좋아요 수 증가
            promptTemplate.incrementFavoriteCount();
            assertThat(promptTemplate.getFavoriteCount()).isEqualTo(1);

            promptTemplate.incrementFavoriteCount();
            assertThat(promptTemplate.getFavoriteCount()).isEqualTo(2);

            // 좋아요 수 감소
            promptTemplate.decrementFavoriteCount();
            assertThat(promptTemplate.getFavoriteCount()).isEqualTo(1);

            promptTemplate.decrementFavoriteCount();
            assertThat(promptTemplate.getFavoriteCount()).isEqualTo(0);

            // 0 이하로는 내려가지 않음
            promptTemplate.decrementFavoriteCount();
            assertThat(promptTemplate.getFavoriteCount()).isEqualTo(0);
        }
    }

    @Test
    @DisplayName("toString 메서드는 content를 제외한 객체 정보를 반환한다")
    void toStringExcludeContent() throws PromptValidationException {
        // given
        UUID id = UUID.randomUUID();
        String title = "테스트 프롬프트";
        String description = "테스트 설명";
        String content = "민감한 내용";

        PromptTemplate promptTemplate = PromptTemplate.builder()
                .id(id)
                .title(title)
                .description(description)
                .content(content)
                .author(author)
                .build();

        // when
        String promptString = promptTemplate.toString();

        // then
        assertThat(promptString).contains(id.toString());
        assertThat(promptString).contains(title);
        assertThat(promptString).contains(description);
        assertThat(promptString).doesNotContain(content);
    }
}
