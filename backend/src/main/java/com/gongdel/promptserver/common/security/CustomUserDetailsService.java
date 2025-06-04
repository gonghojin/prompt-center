package com.gongdel.promptserver.common.security;

import com.gongdel.promptserver.application.port.out.query.*;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.UserRole;
import com.gongdel.promptserver.domain.user.UserDomainException;
import com.gongdel.promptserver.domain.user.UserRoleDomainException;
import com.gongdel.promptserver.domain.role.Role;
import com.gongdel.promptserver.domain.userauth.UserAuthentication;
import com.gongdel.promptserver.domain.userauth.UserAuthenticationDomainException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 사용자 정보를 DB에서 조회하여 UserDetails를 반환하는 서비스입니다.
 * <p>
 * username은 email로 간주합니다.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private static final String DEFAULT_ROLE = "ROLE_USER";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found for email: %s";
    private static final String AUTH_NOT_FOUND_MESSAGE = "UserAuthentication not found for userId: %d";
    private static final String INVALID_EMAIL_MESSAGE = "Invalid email format: %s";
    private static final String NULL_USERNAME_MESSAGE = "Username cannot be null or empty";
    private static final String ROLE_NOT_FOUND_MESSAGE = "Role not found for roleId: %d";

    private final LoadUserPort loadUserPort;
    private final LoadUserAuthenticationPort loadUserAuthenticationPort;
    private final LoadRolePort loadRolePort;
    private final FindUserRolesPort findUserRolesPort;

    /**
     * 이메일(username)로 사용자 정보를 조회하여 UserDetails를 반환합니다.
     *
     * @param username 사용자 이메일
     * @return UserDetails
     * @throws UsernameNotFoundException 사용자 정보가 없을 때
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            validateUsername(username);

            User user = findUserByEmail(username);
            UserAuthentication userAuth = findUserAuthentication(user.getId());
            List<String> roles = findUserRoles(user.getId());

            return new SecurityUserDetails(user, userAuth, roles);
        } catch (UserDomainException | UserAuthenticationDomainException | UserRoleDomainException e) {
            log.error("Domain exception occurred while loading user: {}", e.getMessage());
            throw new UsernameNotFoundException(e.getMessage(), e);
        }
    }

    /**
     * 사용자명의 유효성을 검증합니다.
     *
     * @param username 검증할 사용자명
     * @throws UserDomainException 사용자명이 null이거나 이메일 형식이 아닐 경우
     */
    private void validateUsername(String username) {
        if (!StringUtils.hasText(username)) {
            log.error("Username is null or empty");
            throw new UserDomainException(NULL_USERNAME_MESSAGE);
        }

        try {
            new Email(username);
        } catch (IllegalArgumentException e) {
            log.error("Invalid email format: {}", username);
            throw new UserDomainException(String.format(INVALID_EMAIL_MESSAGE, username));
        }
    }

    /**
     * 이메일로 사용자를 조회합니다.
     *
     * @param email 사용자 이메일
     * @return 조회된 사용자
     * @throws UserDomainException 사용자가 존재하지 않을 경우
     */
    private User findUserByEmail(String email) {
        return loadUserPort.loadUserByEmail(new Email(email))
                .orElseThrow(() -> {
                    log.warn("User not found for email={}", email);
                    return new UserDomainException(String.format(USER_NOT_FOUND_MESSAGE, email));
                });
    }

    /**
     * 사용자 ID로 인증 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 조회된 인증 정보
     * @throws UserAuthenticationDomainException 인증 정보가 존재하지 않을 경우
     */
    private UserAuthentication findUserAuthentication(Long userId) {
        Objects.requireNonNull(userId, "userId cannot be null");

        return loadUserAuthenticationPort.loadUserAuthenticationByUserId(userId)
                .orElseThrow(() -> {
                    log.error("UserAuthentication not found for userId={}", userId);
                    return new UserAuthenticationDomainException(String.format(AUTH_NOT_FOUND_MESSAGE, userId));
                });
    }

    /**
     * 사용자의 권한 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 권한 목록
     * @throws UserRoleDomainException 권한 조회 중 오류가 발생한 경우
     */
    private List<String> findUserRoles(Long userId) {
        Objects.requireNonNull(userId, "userId cannot be null");

        try {
            List<UserRole> userRoles = findUserRolesPort.findUserRolesByUserId(userId);
            if (userRoles.isEmpty()) {
                log.debug("No roles found for userId={}, using default role", userId);
                return Collections.singletonList(DEFAULT_ROLE);
            }

            List<String> roleNames = userRoles.stream()
                    .map(userRole -> loadRolePort.loadRoleById(userRole.getRoleId())
                            .map(Role::getName)
                            .orElseThrow(() -> new UserRoleDomainException(
                                    String.format(ROLE_NOT_FOUND_MESSAGE, userRole.getRoleId()))))
                    .collect(Collectors.toList());

            log.debug("Roles for userId={}: {}", userId, roleNames);
            return roleNames;
        } catch (Exception e) {
            log.error("Error while fetching roles for userId={}", userId, e);
            throw new UserRoleDomainException("Failed to fetch user roles", e);
        }
    }
}
