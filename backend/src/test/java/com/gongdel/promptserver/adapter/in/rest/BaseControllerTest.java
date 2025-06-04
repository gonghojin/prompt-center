package com.gongdel.promptserver.adapter.in.rest;

import com.gongdel.promptserver.common.security.CurrentUserProvider;
import com.gongdel.promptserver.common.security.JwtAuthenticationFilter;
import com.gongdel.promptserver.domain.user.Email;
import com.gongdel.promptserver.domain.user.User;
import com.gongdel.promptserver.domain.user.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.UUID;

import static org.mockito.Mockito.when;

public class BaseControllerTest {

    @MockBean
    protected CurrentUserProvider currentUserProvider;
    @MockBean
    protected JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() {
        User testUser = User.builder()
            .id(1L)
            .uuid(new UserId(UUID.randomUUID()))
            .name("테스트유저")
            .email(new Email("test@example.com"))
            .build();
        when(currentUserProvider.getCurrentUser()).thenReturn(testUser);
    }
}
