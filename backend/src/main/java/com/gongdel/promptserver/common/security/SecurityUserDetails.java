package com.gongdel.promptserver.common.security;

import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.userauth.UserAuthentication;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security의 UserDetails를 구현한 클래스입니다.
 * User, UserAuthentication, 권한 정보를 포함합니다.
 */
@Getter
public class SecurityUserDetails implements UserDetails {
    private static final Logger log = LoggerFactory.getLogger(SecurityUserDetails.class);
    private final User user;
    private final UserAuthentication userAuth;
    private final List<String> roleNames;

    public SecurityUserDetails(User user, UserAuthentication userAuth, List<String> roleNames) {
        this.user = user;
        this.userAuth = userAuth;
        this.roleNames = roleNames;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roleNames.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        log.info(userAuth.getPasswordHash());
        log.info(String.valueOf(new BCryptPasswordEncoder(12).matches("nkia@019",userAuth.getPasswordHash())));
        return userAuth.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getEmail().getValue();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus() != null && user.getStatus().name().equals("ACTIVE");
    }
}
