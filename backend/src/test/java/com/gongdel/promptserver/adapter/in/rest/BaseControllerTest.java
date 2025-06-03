package com.gongdel.promptserver.adapter.in.rest;

import com.gongdel.promptserver.application.usecase.query.TokenValidationService;
import com.gongdel.promptserver.common.security.JwtAuthenticationProvider;
import com.gongdel.promptserver.common.security.JwtTokenProvider;
import com.gongdel.promptserver.config.TestSecurityConfig;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

@Import(TestSecurityConfig.class)
public class BaseControllerTest {

    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    @MockBean
    private JwtAuthenticationProvider jwtAuthenticationProvider;
    @MockBean
    private TokenValidationService tokenValidationService;

}
