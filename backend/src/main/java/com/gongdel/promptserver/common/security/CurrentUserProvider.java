package com.gongdel.promptserver.common.security;

import com.gongdel.promptserver.domain.exception.AuthErrorType;
import com.gongdel.promptserver.domain.exception.AuthException;
import com.gongdel.promptserver.domain.user.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CurrentUserProvider {

    /**
     * 현재 인증된 사용자 정보를 반환합니다.
     *
     * @return 현재 인증된 사용자
     * @throws AuthException 인증되지 않은 경우 또는 인증 정보가 올바르지 않은 경우
     */
    public User getCurrentUser() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        validateAuthentication(authentication);
        final SecurityUserDetails userDetails = extractUserDetails(authentication.getPrincipal());
        return userDetails.getUser();
    }

    /**
     * 인증 객체의 유효성을 검사합니다.
     *
     * @param authentication 인증 객체
     * @throws AuthException 인증 정보가 없거나 인증되지 않은 경우
     */
    private void validateAuthentication(final Authentication authentication) {
        if (authentication == null) {
            log.warn("No authentication found in SecurityContext.");
            throw new AuthException(AuthErrorType.UNAUTHORIZED, "인증 정보가 존재하지 않습니다.");
        }
        if (!authentication.isAuthenticated()) {
            log.warn("Authentication is not valid. Principal: {}", authentication.getPrincipal());
            throw new AuthException(AuthErrorType.UNAUTHORIZED, "인증되지 않은 요청입니다.");
        }
    }

    /**
     * principal 객체에서 SecurityUserDetails를 추출합니다.
     *
     * @param principal 인증 주체 객체
     * @return SecurityUserDetails 객체
     * @throws AuthException principal 타입이 올바르지 않은 경우
     */
    private SecurityUserDetails extractUserDetails(final Object principal) {
        if (!(principal instanceof SecurityUserDetails)) {
            log.warn("Principal is not instance of SecurityUserDetails. Actual type: {}",
                principal != null ? principal.getClass().getName() : "null");
            throw new AuthException(AuthErrorType.UNAUTHORIZED, "인증된 사용자 정보를 찾을 수 없습니다.");
        }
        return (SecurityUserDetails) principal;
    }
}
