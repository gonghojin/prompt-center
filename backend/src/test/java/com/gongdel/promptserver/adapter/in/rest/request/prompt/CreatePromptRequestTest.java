package com.gongdel.promptserver.adapter.in.rest.request.prompt;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CreatePromptRequest DTO 클래스에 대한 테스트
 */
class CreatePromptRequestTest {

    private static final String VALID_TITLE = "테스트 제목";
    private static final String VALID_DESCRIPTION = "테스트 설명";
    private static final String VALID_CONTENT = "테스트 내용";
    private static final Long VALID_CATEGORY_ID = 1L;
    private static final String VALID_VISIBILITY = "PUBLIC";
    private static final String VALID_STATUS = "DRAFT";
    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    private CreatePromptRequest.CreatePromptRequestBuilder validBuilder() {
        return CreatePromptRequest.builder()
            .title(VALID_TITLE)
            .description(VALID_DESCRIPTION)
            .content(VALID_CONTENT)
            .categoryId(VALID_CATEGORY_ID)
            .visibility(VALID_VISIBILITY)
            .status(VALID_STATUS);
    }

    @Test
    @DisplayName("CreatePromptRequest 빌더를 사용하여 유효한 DTO 객체를 생성할 수 있다")
    void builderShouldCreateValidDto() {
        // given
        // when
        CreatePromptRequest request = validBuilder().build();

        // then
        assertThat(request).isNotNull();
        assertThat(request.getTitle()).isEqualTo(VALID_TITLE);
        assertThat(request.getDescription()).isEqualTo(VALID_DESCRIPTION);
        assertThat(request.getContent()).isEqualTo(VALID_CONTENT);
        assertThat(request.getCategoryId()).isEqualTo(VALID_CATEGORY_ID);
        assertThat(request.getVisibility()).isEqualTo(VALID_VISIBILITY);
        assertThat(request.getStatus()).isEqualTo(VALID_STATUS);

        // 유효성 검증
        Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("기본 생성자와 세터를 사용하여 DTO 객체를 생성할 수 있다")
    void defaultConstructorAndSettersShouldCreateValidDto() {
        // given
        CreatePromptRequest request = new CreatePromptRequest();
        request.setTitle(VALID_TITLE);
        request.setDescription(VALID_DESCRIPTION);
        request.setContent(VALID_CONTENT);
        request.setCategoryId(VALID_CATEGORY_ID);
        request.setVisibility(VALID_VISIBILITY);
        request.setStatus(VALID_STATUS);

        // then
        assertThat(request).isNotNull();
        assertThat(request.getTitle()).isEqualTo(VALID_TITLE);
        assertThat(request.getDescription()).isEqualTo(VALID_DESCRIPTION);
        assertThat(request.getContent()).isEqualTo(VALID_CONTENT);
        assertThat(request.getCategoryId()).isEqualTo(VALID_CATEGORY_ID);
        assertThat(request.getVisibility()).isEqualTo(VALID_VISIBILITY);
        assertThat(request.getStatus()).isEqualTo(VALID_STATUS);

        // 유효성 검증
        Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("tagIds가 null이면 기본값으로 빈 HashSet이 설정된다")
    void tagsShouldDefaultToEmptySet() {
        // when
        CreatePromptRequest request = validBuilder().build();

        // then
        assertThat(request.getTags()).isNotNull();
        assertThat(request.getTags()).isEmpty();
    }

    @Nested
    @DisplayName("유효성 검증 테스트")
    class ValidationTests {

        @Test
        @DisplayName("제목이 null이면 유효성 검증에 실패한다")
        void titleShouldNotBeNull() {
            // given
            CreatePromptRequest request = validBuilder().title(null).build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("title") &&
                    violation.getMessage().equals("Title is required"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("제목이 비어있거나 공백만 있으면 유효성 검증에 실패한다")
        void titleShouldNotBeBlank(String blankTitle) {
            // given
            CreatePromptRequest request = validBuilder().title(blankTitle).build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations)
                .anyMatch(violation -> violation.getPropertyPath().toString().equals("title") &&
                    violation.getMessage().equals("Title is required"));
        }

        @Test
        @DisplayName("제목이 200자를 초과하면 유효성 검증에 실패한다")
        void titleShouldNotExceed200Characters() {
            // given
            String longTitle = "a".repeat(201);
            CreatePromptRequest request = validBuilder().title(longTitle).build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString()
                .equals("title") &&
                violation.getMessage().equals("Title must be less than 200 characters"));
        }

        @Test
        @DisplayName("설명이 1000자를 초과하면 유효성 검증에 실패한다")
        void descriptionShouldNotExceed1000Characters() {
            // given
            String longDescription = "a".repeat(1001);
            CreatePromptRequest request = validBuilder().description(longDescription).build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString()
                .equals("description") &&
                violation.getMessage().equals("Description must be less than 1000 characters"));
        }

        @Test
        @DisplayName("내용이 null이면 유효성 검증에 실패한다")
        void contentShouldNotBeNull() {
            // given
            CreatePromptRequest request = validBuilder().content(null).build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                violation -> violation.getPropertyPath().toString().equals("content") &&
                    violation.getMessage().equals("Content is required"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("내용이 비어있거나 공백만 있으면 유효성 검증에 실패한다")
        void contentShouldNotBeBlank(String blankContent) {
            // given
            CreatePromptRequest request = validBuilder().content(blankContent).build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                violation -> violation.getPropertyPath().toString().equals("content") &&
                    violation.getMessage().equals("Content is required"));
        }

        @Test
        @DisplayName("내용이 20000자를 초과하면 유효성 검증에 실패한다")
        void contentShouldNotExceed20000Characters() {
            // given
            String longContent = "a".repeat(20001);
            CreatePromptRequest request = validBuilder().content(longContent).build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString()
                .equals("content") &&
                violation.getMessage().equals("Content must be less than 20000 characters"));
        }

        @Test
        @DisplayName("categoryId가 null이면 유효성 검증에 실패한다")
        void categoryIdShouldNotBeNull() {
            // given
            CreatePromptRequest request = validBuilder().categoryId(null).build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                violation -> violation.getPropertyPath().toString().equals("categoryId") &&
                    violation.getMessage().equals("CategoryId is required"));
        }

        @Test
        @DisplayName("visibility가 null이면 유효성 검증에 실패한다")
        void visibilityShouldNotBeNull() {
            // given
            CreatePromptRequest request = validBuilder().visibility(null).build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                violation -> violation.getPropertyPath().toString().equals("visibility") &&
                    violation.getMessage().equals("Visibility is required"));
        }

        @Test
        @DisplayName("status가 null이면 유효성 검증에 실패한다")
        void statusShouldNotBeNull() {
            // given
            CreatePromptRequest request = validBuilder().status(null).build();

            // when
            Set<ConstraintViolation<CreatePromptRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(
                violation -> violation.getPropertyPath().toString().equals("status") &&
                    violation.getMessage().equals("Status is required"));
        }
    }

    @Nested
    class LombokTest {
        @Test
        void 빌더_정상동작() {
            // Given
            CreatePromptRequest request = validBuilder().build();
            // When & Then
            assertThat(request.getTitle()).isEqualTo(VALID_TITLE);
            assertThat(request.getDescription()).isEqualTo(VALID_DESCRIPTION);
            assertThat(request.getContent()).isEqualTo(VALID_CONTENT);
            assertThat(request.getCategoryId()).isEqualTo(VALID_CATEGORY_ID);
            assertThat(request.getVisibility()).isEqualTo(VALID_VISIBILITY);
            assertThat(request.getStatus()).isEqualTo(VALID_STATUS);
        }

        @Test
        void 세터_게터_정상동작() {
            // Given
            CreatePromptRequest request = new CreatePromptRequest();
            request.setTitle(VALID_TITLE);
            request.setDescription(VALID_DESCRIPTION);
            request.setContent(VALID_CONTENT);
            request.setCategoryId(VALID_CATEGORY_ID);
            request.setVisibility(VALID_VISIBILITY);
            request.setStatus(VALID_STATUS);
            // When & Then
            assertThat(request.getTitle()).isEqualTo(VALID_TITLE);
            assertThat(request.getDescription()).isEqualTo(VALID_DESCRIPTION);
            assertThat(request.getContent()).isEqualTo(VALID_CONTENT);
            assertThat(request.getCategoryId()).isEqualTo(VALID_CATEGORY_ID);
            assertThat(request.getVisibility()).isEqualTo(VALID_VISIBILITY);
            assertThat(request.getStatus()).isEqualTo(VALID_STATUS);
        }
    }

    @Nested
    class UserDtoTest {
        @Test
        void UserDto_빌더_게터_세터_정상동작() {
            // Given
            UUID id = UUID.randomUUID();
            String email = "test@email.com";
            String name = "홍길동";
            CreatePromptRequest.UserDto userDto = CreatePromptRequest.UserDto.builder()
                .id(id)
                .email(email)
                .name(name)
                .build();
            // When & Then
            assertThat(userDto.getId()).isEqualTo(id);
            assertThat(userDto.getEmail()).isEqualTo(email);
            assertThat(userDto.getName()).isEqualTo(name);

            // 세터 테스트
            CreatePromptRequest.UserDto userDto2 = new CreatePromptRequest.UserDto();
            userDto2.setId(id);
            userDto2.setEmail(email);
            userDto2.setName(name);
            assertThat(userDto2.getId()).isEqualTo(id);
            assertThat(userDto2.getEmail()).isEqualTo(email);
            assertThat(userDto2.getName()).isEqualTo(name);
        }
    }
}
