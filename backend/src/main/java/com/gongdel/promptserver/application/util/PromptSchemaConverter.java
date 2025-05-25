package com.gongdel.promptserver.application.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 프롬프트 변수 스키마 자동 변환 유틸리티
 */
public class PromptSchemaConverter {

    private static final String KEY_TYPE = "type";
    private static final String KEY_DEFAULT = "default";
    private static final String TYPE_STRING = "string";
    private static final String TYPE_INTEGER = "integer";
    private static final String TYPE_BOOLEAN = "boolean";
    private static final String TYPE_NUMBER = "number";

    /**
     * 단순 값 형태의 variablesSchema를 표준 스키마로 변환합니다. 예시: 입력: { "user": "홍길동", "age": 30 } 출력: { "user": { "type": "string",
     * "default": "홍길동" }, "age": { "type": "integer", "default": 30 } }
     *
     * @param rawSchema 클라이언트가 보낸 단순 값 형태의 스키마
     * @return 표준 스키마
     */
    public static Map<String, Object> convertToStandardSchema(Map<String, Object> rawSchema) {
        if (rawSchema == null) {
            return new HashMap<>();
        }
        Map<String, Object> standardSchema = new HashMap<>();
        for (Map.Entry<String, Object> entry : rawSchema.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map && ((Map<?, ?>) value).containsKey(KEY_TYPE)) {
                // 이미 표준 스키마라면 그대로 사용
                standardSchema.put(key, value);
                continue;
            }
            Map<String, Object> varSchema = new HashMap<>();
            // 타입 추론 및 기본값 설정
            if (value instanceof String) {
                varSchema.put(KEY_TYPE, TYPE_STRING);
                varSchema.put(KEY_DEFAULT, value);
            } else if (value instanceof Integer) {
                varSchema.put(KEY_TYPE, TYPE_INTEGER);
                varSchema.put(KEY_DEFAULT, value);
            } else if (value instanceof Boolean) {
                varSchema.put(KEY_TYPE, TYPE_BOOLEAN);
                varSchema.put(KEY_DEFAULT, value);
            } else if (value instanceof Double || value instanceof Float) {
                varSchema.put(KEY_TYPE, TYPE_NUMBER);
                varSchema.put(KEY_DEFAULT, value);
            } else {
                // 기타 타입은 string으로 처리
                varSchema.put(KEY_TYPE, TYPE_STRING);
                varSchema.put(KEY_DEFAULT, String.valueOf(value));
            }
            standardSchema.put(key, varSchema);
        }
        return standardSchema;
    }
}
