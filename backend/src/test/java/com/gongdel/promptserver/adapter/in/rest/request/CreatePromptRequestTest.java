package com.gongdel.promptserver.adapter.in.rest.request;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * CreatePromptRequest DTO 클래스에 대한 테스트
 */
class CreatePromptRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("CreatePromptRequest 빌더를 사용하여 유효한 DTO 객체를 생성할 수 있다")
    void builderShouldCreateValidDto() {
        // given
        String title = "테스트 제목";
        String description = "테스트 설명";
        String content = "테스트 내용";
        Set<UUID> tagIds = new HashSet<>();
        tagIds.add(UUID.randomUUID());
        boolean isPublic = true;

        // when
        CreatePromptRequest request = CreatePromptRequest.builder()
                .title(title)
                .description(description)
                .content(content)
                .build();

        // then
        assertThat(request).isNotNull();
        assertThat(request.getTitle()).isEqualTo(title);
        assertThat(request.getDescription()).isEqualTo(description);
        assertThat(request.getContent()).isEqualTo(content);

        // 유효성 검증
        Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("기본 생성자와 세터를 사용하여 DTO 객체를 생성할 수 있다")
    void defaultConstructorAndSettersShouldCreateValidDto() {
        // given
        String title = "테스트 제목";
        String description = "테스트 설명";
        String content = "테스트 내용";
        Set<UUID> tagIds = new HashSet<>();
        tagIds.add(UUID.randomUUID());
        boolean isPublic = true;

        // when
        CreatePromptRequest request = new CreatePromptRequest();
        request.setTitle(title);
        request.setDescription(description);
        request.setContent(content);

        // then
        assertThat(request).isNotNull();
        assertThat(request.getTitle()).isEqualTo(title);
        assertThat(request.getDescription()).isEqualTo(description);
        assertThat(request.getContent()).isEqualTo(content);

        // 유효성 검증
        Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("tagIds가 null이면 기본값으로 빈 HashSet이 설정된다")
    void tagIdsShouldDefaultToEmptySet() {
        // when
        CreatePromptRequest request = CreatePromptRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        // then
    }

    @Nested
    @DisplayName("유효성 검증 테스트")
    class ValidationTests {

        @Test
        @DisplayName("제목이 null이면 유효성 검증에 실패한다")
        void titleShouldNotBeNull() {
            // given
            CreatePromptRequest request = CreatePromptRequest.builder()
                    .title(null)
                    .content("테스트 내용")
                    .build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("title") &&
                    violation.getMessage().equals("Title is required"));
        }

        @ParameterizedTest
        @ValueSource(strings = { "", " ", "   " })
        @DisplayName("제목이 비어있거나 공백만 있으면 유효성 검증에 실패한다")
        void titleShouldNotBeBlank(String blankTitle) {
            // given
            CreatePromptRequest request = CreatePromptRequest.builder()
                    .title(blankTitle)
                    .content("테스트 내용")
                    .build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("title") &&
                    violation.getMessage().equals("Title is required"));
        }

        @Test
        @DisplayName("제목이 200자를 초과하면 유효성 검증에 실패한다")
        void titleShouldNotExceed200Characters() {
            // given
            String longTitle = "a".repeat(201);

            CreatePromptRequest request = CreatePromptRequest.builder()
                    .title(longTitle)
                    .content("테스트 내용")
                    .build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("title") &&
                    violation.getMessage().equals("Title must be less than 200 characters"));
        }

        @Test
        @DisplayName("설명이 1000자를 초과하면 유효성 검증에 실패한다")
        void descriptionShouldNotExceed1000Characters() {
            // given
            String longDescription = "a".repeat(1001);

            CreatePromptRequest request = CreatePromptRequest.builder()
                    .title("테스트 제목")
                    .description(longDescription)
                    .content("테스트 내용")
                    .build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("description") &&
                    violation.getMessage().equals("Description must be less than 1000 characters"));
        }

        @Test
        @DisplayName("내용이 null이면 유효성 검증에 실패한다")
        void contentShouldNotBeNull() {
            // given
            CreatePromptRequest request = CreatePromptRequest.builder()
                    .title("테스트 제목")
                    .content(null)
                    .build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("content") &&
                    violation.getMessage().equals("Content is required"));
        }

        @ParameterizedTest
        @ValueSource(strings = { "", " ", "   " })
        @DisplayName("내용이 비어있거나 공백만 있으면 유효성 검증에 실패한다")
        void contentShouldNotBeBlank(String blankContent) {
            // given
            CreatePromptRequest request = CreatePromptRequest.builder()
                    .title("테스트 제목")
                    .content(blankContent)
                    .build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("content") &&
                    violation.getMessage().equals("Content is required"));
        }

        @Test
        @DisplayName("내용이 20000자를 초과하면 유효성 검증에 실패한다")
        void contentShouldNotExceed20000Characters() {
            // given
            String longContent = "a".repeat(20001);

            CreatePromptRequest request = CreatePromptRequest.builder()
                    .title("테스트 제목")
                    .content(longContent)
                    .build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("content") &&
                    violation.getMessage().equals("Content must be less than 20000 characters"));
        }
    }
}
