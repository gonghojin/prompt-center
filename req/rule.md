# 코드 스타일 규칙 가이드

## 1. 개요

이 문서는 프로젝트의 코드 스타일 규칙과 설정 방법을 설명합니다. 프로젝트는 구글 스타일 가이드를 기반으로 합니다.

## 2. 설정 파일 구조

```
.
├── .editorconfig              # 에디터 기본 설정
├── .vscode/
│   └── settings.json         # Cursor/VS Code 설정
├── backend/
│   ├── config/
│   │   └── checkstyle/
│   │       └── google_checks.xml # Google Java 스타일 규칙
│   └── build.gradle          # Gradle 설정 (Checkstyle 포함)
└── frontend/
    ├── .eslintrc.js          # Google JavaScript 스타일 적용 ESLint 설정
    ├── .prettierrc           # Google 스타일 기반 Prettier 설정
    └── package.json          # eslint-config-google 패키지 포함
```

## 3. 자동 포맷팅 설정

### 3.1 Cursor/VS Code 설정

`.vscode/settings.json`에 다음 설정이 포함되어 있습니다:

```json
{
  "editor.formatOnSave": true,           // 저장 시 자동 포맷팅
  "editor.codeActionsOnSave": {          // 저장 시 자동 수정
    "source.fixAll.eslint": true
  },
  "editor.defaultFormatter": "esbenp.prettier-vscode",  // 기본 포맷터
  "[java]": {                            // Java 파일 포맷터
    "editor.defaultFormatter": "redhat.java",
    "editor.tabSize": 2                  // Google Java 스타일 - 2칸 들여쓰기
  },
  "editor.rulers": [100],               // Google 스타일 가이드 기준 줄 길이
  "files.trimTrailingWhitespace": true,  // 줄 끝 공백 제거
  "files.insertFinalNewline": true,      // 파일 끝 빈 줄 추가
  "files.trimFinalNewlines": true        // 파일 끝 빈 줄 정리
}
```

### 3.2 EditorConfig 설정

`.editorconfig`에 정의된 기본 규칙 (Google 스타일 기준):

```ini
# 모든 파일 공통
root = true

[*]
charset = utf-8
end_of_line = lf
insert_final_newline = true
trim_trailing_whitespace = true

# Java 파일 (Google 스타일)
[*.java]
indent_style = space
indent_size = 2
max_line_length = 100

# JavaScript/TypeScript 파일 (Google 스타일)
[*.{js,jsx,ts,tsx}]
indent_style = space
indent_size = 2
max_line_length = 80
```

## 4. 구글 스타일 가이드 적용 방법

### 4.1 Java (Backend)

1. `google_checks.xml` 파일 추가:
    - Checkstyle 공식 사이트에서 최신 버전의 `google_checks.xml` 다운로드
    - `backend/config/checkstyle/` 디렉토리에 저장

2. Gradle 설정 (build.gradle):

```gradle
plugins {
    id 'checkstyle'
}

checkstyle {
    toolVersion = '8.45'  // 최신 버전 사용 권장
    configFile = file("${project.rootDir}/config/checkstyle/google_checks.xml")
}
```

### 4.2 JavaScript/TypeScript (Frontend)

1. 필요한 패키지 설치:

```bash
npm install --save-dev eslint eslint-config-google prettier
```

2. ESLint 설정 (.eslintrc.js):

```javascript
module.exports = {
  extends: ['google', 'prettier'],
  rules: {
    // 프로젝트 특정 예외 규칙
  }
};
```

3. Prettier 설정 (.prettierrc):

```json
{
  "singleQuote": true,
  "trailingComma": "es5",
  "bracketSpacing": false,
  "arrowParens": "always",
  "printWidth": 80
}
```

## 5. 활용 방법

### 5.1 자동 포맷팅

- 파일 저장 시 자동으로 포맷팅 적용
- 단축키: `Cmd/Ctrl + S` (저장)

### 5.2 수동 포맷팅

- 전체 파일: `Cmd/Ctrl + Shift + F`
- 선택 영역: `Cmd/Ctrl + K, Cmd/Ctrl + F`

### 5.3 문제 수정

- `Cmd/Ctrl + .`: 빠른 수정 제안
- `Cmd/Ctrl + Shift + P` → "Fix all auto-fixable problems"

## 6. 구글 스타일 가이드 주요 규칙

### 6.1 Java

- 2칸 들여쓰기
- 최대 줄 길이 100자
- 변수 이름: camelCase
- 상수: UPPER_SNAKE_CASE
- 클래스 이름: PascalCase
- 중괄호는 새 줄에 시작하지 않음
- Javadoc 주석 필수 (공개 API)

### 6.2 JavaScript/TypeScript

- 2칸 들여쓰기
- 최대 줄 길이 80자
- 변수/함수 이름: camelCase
- 클래스 이름: PascalCase
- 상수: UPPER_SNAKE_CASE
- 세미콜론 필수
- 문자열에 작은따옴표 사용

## 7. 문제 해결

### 7.1 자동 포맷팅이 작동하지 않을 때

1. Cursor/VS Code 재시작
2. 명령어 팔레트(`Cmd/Ctrl + Shift + P`)에서:
    - "Format Document" 실행
    - "Fix all auto-fixable problems" 실행

### 7.2 특정 파일 포맷팅이 안 될 때

1. 파일 확장자 확인
2. 해당 언어의 포맷터가 설치되어 있는지 확인
3. `.vscode/settings.json`의 언어별 설정 확인

## 8. 권장 사항

1. **커밋 전 확인**
    - 코드 스타일 규칙 준수 여부 확인
    - 불필요한 공백, 줄바꿈 제거

2. **팀 협업**
    - 동일한 설정 파일 사용
    - 코드 리뷰 시 스타일 규칙 준수 확인

3. **IDE 설정**
    - EditorConfig 플러그인 설치
    - ESLint, Prettier 플러그인 설치 (프론트엔드)
    - Checkstyle 플러그인 설치 (백엔드)
    - Google 스타일 가이드 문서 참조 (공식 문서)
