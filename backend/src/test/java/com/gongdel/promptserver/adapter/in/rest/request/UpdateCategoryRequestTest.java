package com.gongdel.promptserver.adapter.in.rest.request;

import com.gongdel.promptserver.application.port.in.command.UpdateCategoryCommand;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UpdateCategoryRequest DTO 클래스에 대한 테스트
 */
class UpdateCategoryRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("UpdateCategoryRequest 빌더를 사용하여 유효한 DTO 객체를 생성할 수 있다")
    void builderShouldCreateValidDto() {
        // given
        String displayName = "업데이트된 카테고리";
        String description = "업데이트된 카테고리 설명";
        Long parentCategoryId = 2L;

        // when
        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
            .displayName(displayName)
            .description(description)
            .parentCategoryId(parentCategoryId)
            .build();

        // then
        assertThat(request).isNotNull();
        assertThat(request.getDisplayName()).isEqualTo(displayName);
        assertThat(request.getDescription()).isEqualTo(description);
        assertThat(request.getParentCategoryId()).isEqualTo(parentCategoryId);

        // 유효성 검증
        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("기본 생성자와 세터를 사용하여 DTO 객체를 생성할 수 있다")
    void defaultConstructorAndSettersShouldCreateValidDto() {
        // given
        String displayName = "업데이트된 카테고리";
        String description = "업데이트된 카테고리 설명";
        Long parentCategoryId = 2L;

        // when
        UpdateCategoryRequest request = new UpdateCategoryRequest();
        request.setDisplayName(displayName);
        request.setDescription(description);
        request.setParentCategoryId(parentCategoryId);

        // then
        assertThat(request).isNotNull();
        assertThat(request.getDisplayName()).isEqualTo(displayName);
        assertThat(request.getDescription()).isEqualTo(description);
        assertThat(request.getParentCategoryId()).isEqualTo(parentCategoryId);

        // 유효성 검증
        Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);
        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("toCommand 메서드가 올바른 UpdateCategoryCommand 객체를 반환해야 한다")
    void toCommandShouldReturnCorrectCommand() {
        // given
        Long categoryId = 1L;
        String displayName = "업데이트된 카테고리";
        String description = "업데이트된 카테고리 설명";
        Long parentCategoryId = 2L;

        UpdateCategoryRequest request = UpdateCategoryRequest.builder()
            .displayName(displayName)
            .description(description)
            .parentCategoryId(parentCategoryId)
            .build();

        // when
        UpdateCategoryCommand command = request.toCommand(categoryId);

        // then
        assertThat(command).isNotNull();
        assertThat(command.getId()).isEqualTo(categoryId);
        assertThat(command.getDisplayName()).isEqualTo(displayName);
        assertThat(command.getDescription()).isEqualTo(description);
        assertThat(command.getParentCategoryId()).isEqualTo(parentCategoryId);
    }

    @Nested
    @DisplayName("유효성 검증 테스트")
    class ValidationTests {

        @Test
        @DisplayName("표시 이름이 null이면 유효성 검증에 실패한다")
        void displayNameShouldNotBeNull() {
            // given
            UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .displayName(null)
                .description("테스트 설명")
                .build();

            // when
            Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("displayName") &&
                violation.getMessage().equals("카테고리 표시 이름은 필수입니다"));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "   "})
        @DisplayName("표시 이름이 비어있거나 공백만 있으면 유효성 검증에 실패한다")
        void displayNameShouldNotBeBlank(String blankDisplayName) {
            // given
            UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .displayName(blankDisplayName)
                .description("테스트 설명")
                .build();

            // when
            Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(violation -> violation.getPropertyPath().toString().equals("displayName") &&
                violation.getMessage().equals("카테고리 표시 이름은 필수입니다"));
        }

        @Test
        @DisplayName("설명이 null이면 유효성 검증에 통과한다")
        void descriptionCanBeNull() {
            // given
            UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .displayName("업데이트된 카테고리")
                .description(null)
                .build();

            // when
            Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("상위 카테고리 ID가 null이면 유효성 검증에 통과한다")
        void parentCategoryIdCanBeNull() {
            // given
            UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .displayName("업데이트된 카테고리")
                .parentCategoryId(null)
                .build();

            // when
            Set<ConstraintViolation<UpdateCategoryRequest>> violations = validator.validate(request);

            // then
            assertThat(violations).isEmpty();
        }

        @Test
        @DisplayName("toCommand 메서드에 null ID 전달 시 NullPointerException 발생하지 않는지 확인")
        void toCommandShouldHandleNullId() {
            // given
            UpdateCategoryRequest request = UpdateCategoryRequest.builder()
                .displayName("업데이트된 카테고리")
                .build();

            // when
            UpdateCategoryCommand command = request.toCommand(1L);

            // then
            assertThat(command).isNotNull();
            assertThat(command.getId()).isEqualTo(1L);
        }
    }
}
