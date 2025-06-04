package com.gongdel.promptserver.domain.user;

public interface UserRepository {
    boolean existsByEmail(Email email);
}
