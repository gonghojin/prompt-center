package com.gongdel.promptserver.domain.user;

import com.gongdel.promptserver.domain.team.Team;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 사용자 관련 도메인 서비스입니다. 도메인 객체만으로 표현하기 어려운 비즈니스 규칙을 담당합니다.
 */
@Component
public class UserDomainService {
    private static final String EMAIL_DUPLICATION_MESSAGE = "이미 사용 중인 이메일입니다.";
    private static final String PASSWORD_LETTER_REQUIRED = "비밀번호에는 영문자가 포함되어야 합니다.";
    private static final String PASSWORD_NUMBER_REQUIRED = "비밀번호에는 숫자가 포함되어야 합니다.";
    private static final String PASSWORD_SPECIAL_CHAR_REQUIRED = "비밀번호에는 특수문자가 포함되어야 합니다.";
    private static final String PASSWORD_LENGTH_REQUIRED = "비밀번호는 8자 이상이어야 합니다.";

    private static final String LETTER_PATTERN = ".*[A-Za-z].*";
    private static final String NUMBER_PATTERN = ".*[0-9].*";
    private static final String SPECIAL_CHAR_PATTERN = ".*[!@#$%^&*()].*";
    private static final int MIN_PASSWORD_LENGTH = 8;

    // TODO: 비즈니스 검증 개선하기 아래 소스
    /**
     * 이메일 중복 여부를 검증합니다.
     *
     * @param email          검증할 이메일
     * @param userRepository 사용자 리포지토리(도메인 계층 인터페이스)
     * @throws UserDomainException      이미 사용 중인 이메일일 경우
     * @throws IllegalArgumentException 파라미터가 null인 경우
     */
    public void validateEmailDuplication(Email email, UserRepository userRepository) {
        Assert.notNull(email, "이메일은 null일 수 없습니다.");
        Assert.notNull(userRepository, "사용자 리포지토리는 null일 수 없습니다.");

        if (userRepository.existsByEmail(email)) {
            throw new UserDomainException(EMAIL_DUPLICATION_MESSAGE);
        }
    }

    /**
     * 비밀번호 정책을 검증합니다.
     *
     * @param password 검증할 비밀번호
     * @throws UserDomainException      정책 위반 시
     * @throws IllegalArgumentException 파라미터가 null인 경우
     */
    public void validatePasswordPolicy(Password password) {
        Assert.notNull(password, "비밀번호는 null일 수 없습니다.");

        String value = password.toRaw();
        if (value.length() < MIN_PASSWORD_LENGTH) {
            throw new UserDomainException(PASSWORD_LENGTH_REQUIRED);
        }

        validatePasswordPattern(value, LETTER_PATTERN, PASSWORD_LETTER_REQUIRED);
        validatePasswordPattern(value, NUMBER_PATTERN, PASSWORD_NUMBER_REQUIRED);
        validatePasswordPattern(value, SPECIAL_CHAR_PATTERN, PASSWORD_SPECIAL_CHAR_REQUIRED);
    }

    /**
     * 비밀번호 패턴을 검증합니다.
     *
     * @param password 검증할 비밀번호
     * @param pattern  검증할 패턴
     * @param message  실패 시 메시지
     * @throws UserDomainException 패턴 불일치 시
     */
    private void validatePasswordPattern(String password, String pattern, String message) {
        if (!password.matches(pattern)) {
            throw new UserDomainException(message);
        }
    }
}
