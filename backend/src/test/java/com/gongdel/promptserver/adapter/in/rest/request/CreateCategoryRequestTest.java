package com.gongdel.promptserver.adapter.in.rest.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.gongdel.promptserver.application.port.in.command.CreateCategoryCommand;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * CreateCategoryRequest DTO 클래스에 대한 테스트
 */
class CreateCategoryRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("CreateCategoryRequest 빌더를 사용하여 유효한 DTO 객체를 생성할 수 있다")
    void builderShouldCreateValidDto() {
        // given
        String name = "test-category";
        String displayName = "테스트 카테고리";
        String description = "테스트용 카테고리입니다";
        Long parentCategoryId = 1L;
        boolean isSystem = true;

        // when
        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name(name)
                .displayName(displayName)
                .description(description)
                .parentCategoryId(parentCategoryId)
                .isSystem(isSystem)
                .build();

        // then
        assertThat(request).isNotNull();
        assertThat(request.getName()).isEqualTo(name);
        assertThat(request.getDisplayName()).isEqualTo(displayName);
        assertThat(request.getDescription()).isEqualTo(description);
        assertThat(request.getParentCategoryId()).isEqualTo(parentCategoryId);
        assertThat(request.isSystem()).isEqualTo(isSystem);

        // 유효성 검증
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("기본 생성자와 세터를 사용하여 DTO 객체를 생성할 수 있다")
    void defaultConstructorAndSettersShouldCreateValidDto() {
        // given
        String name = "test-category";
        String displayName = "테스트 카테고리";
        String description = "테스트용 카테고리입니다";
        Long parentCategoryId = 1L;
        boolean isSystem = true;

        // when
        CreateCategoryRequest request = new CreateCategoryRequest();
        request.setName(name);
        request.setDisplayName(displayName);
        request.setDescription(description);
        request.setParentCategoryId(parentCategoryId);
        request.setSystem(isSystem);

        // then
        assertThat(request).isNotNull();
        assertThat(request.getName()).isEqualTo(name);
        assertThat(request.getDisplayName()).isEqualTo(displayName);
        assertThat(request.getDescription()).isEqualTo(description);
        assertThat(request.getParentCategoryId()).isEqualTo(parentCategoryId);
        assertThat(request.isSystem()).isEqualTo(isSystem);

        // 유효성 검증
        Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("toCommand 메서드가 올바른 CreateCategoryCommand 객체를 반환해야 한다")
    void toCommandShouldReturnCorrectCommand() {
        // given
        String name = "test-category";
        String displayName = "테스트 카테고리";
        String description = "테스트용 카테고리입니다";
        Long parentCategoryId = 1L;
        boolean isSystem = true;

        CreateCategoryRequest request = CreateCategoryRequest.builder()
                .name(name)
                .displayName(displayName)
                .description(description)
                .parentCategoryId(parentCategoryId)
                .isSystem(isSystem)
                .build();

        // when
        CreateCategoryCommand command = request.toCommand();

        // then
        assertThat(command).isNotNull();
        assertThat(command.getName()).isEqualTo(name);
        assertThat(command.getDisplayName()).isEqualTo(displayName);
        assertThat(command.getDescription()).isEqualTo(description);
        assertThat(command.getParentCategoryId()).isEqualTo(parentCategoryId);
        assertThat(command.isSystem()).isEqualTo(isSystem);
    }

    @Nested
    @DisplayName("유효성 검증 테스트")
    class ValidationTests {

        @Test
        @DisplayName("이름이 null이면 유효성 검증에 실패한다")
        void nameShouldNotBeNull() {
            // given
            CreateCategoryRequest request = CreateCategoryRequest.builder()
                    .name(null)
                    .displayName("테스트 카테고리")
                    .build();

            // when
            Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("name") &&
                    violation.getMessage().equals("카테고리 이름은 필수입니다"));
        }

        @ParameterizedTest
        @ValueSource(strings = { "", " ", "   " })
        @DisplayName("이름이 비어있거나 공백만 있으면 유효성 검증에 실패한다")
        void nameShouldNotBeBlank(String blankName) {
            // given
            CreateCategoryRequest request = CreateCategoryRequest.builder()
                    .name(blankName)
                    .displayName("테스트 카테고리")
                    .build();

            // when
            Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("name") &&
                    violation.getMessage().equals("카테고리 이름은 필수입니다"));
        }

        @Test
        @DisplayName("표시 이름이 null이면 유효성 검증에 실패한다")
        void displayNameShouldNotBeNull() {
            // given
            CreateCategoryRequest request = CreateCategoryRequest.builder()
                    .name("test-category")
                    .displayName(null)
                    .build();

            // when
            Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("displayName") &&
                    violation.getMessage().equals("카테고리 표시 이름은 필수입니다"));
        }

        @ParameterizedTest
        @ValueSource(strings = { "", " ", "   " })
        @DisplayName("표시 이름이 비어있거나 공백만 있으면 유효성 검증에 실패한다")
        void displayNameShouldNotBeBlank(String blankDisplayName) {
            // given
            CreateCategoryRequest request = CreateCategoryRequest.builder()
                    .name("test-category")
                    .displayName(blankDisplayName)
                    .build();

            // when
            Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("displayName") &&
                    violation.getMessage().equals("카테고리 표시 이름은 필수입니다"));
        }

        @Test
        @DisplayName("설명이 null이면 유효성 검증에 통과한다")
        void descriptionCanBeNull() {
            // given
            CreateCategoryRequest request = CreateCategoryRequest.builder()
                    .name("test-category")
                    .displayName("테스트 카테고리")
                    .description(null)
                    .build();

            // when
            Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("상위 카테고리 ID가 null이면 유효성 검증에 통과한다")
        void parentCategoryIdCanBeNull() {
            // given
            CreateCategoryRequest request = CreateCategoryRequest.builder()
                    .name("test-category")
                    .displayName("테스트 카테고리")
                    .parentCategoryId(null)
                    .build();

            // when
            Set<ConstraintViolation<CreateCategoryRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }
    }
}
