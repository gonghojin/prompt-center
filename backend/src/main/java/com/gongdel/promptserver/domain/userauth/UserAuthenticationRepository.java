package com.gongdel.promptserver.domain.userauth;

import java.util.Optional;

public interface UserAuthenticationRepository {
    Optional<UserAuthentication> findByUserId(Long userId);

    UserAuthentication save(UserAuthentication userAuthentication);
}
