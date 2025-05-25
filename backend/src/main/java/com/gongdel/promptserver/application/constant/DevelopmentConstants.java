package com.gongdel.promptserver.application.constant;

/**
 * 개발 환경에서 사용되는 상수들을 정의합니다. 이 클래스의 상수들은 실제 운영 환경에서는 사용되지 않아야 합니다.
 */
public class DevelopmentConstants {

    public static final Long TEMP_USER_ID = 1L;
    public static final String TEMP_USER_UUID = "00000000-0000-0000-0000-000000000000";
    public static final String TEMP_USER_EMAIL = "temp@system.local";
    public static final String TEMP_USER_NAME = "System User";

    private DevelopmentConstants() {
        // 인스턴스화 방지
    }
}
