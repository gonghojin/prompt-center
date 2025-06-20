# Prompth Center Backend

## 코드 테스트 및 커버리지 측정

이 프로젝트는 JaCoCo를 사용하여 코드 커버리지를 측정합니다.

### 기본 테스트 실행 및 커버리지 보고서 생성

다음 명령어로 테스트를 실행하고 JaCoCo 커버리지 보고서를 생성할 수 있습니다:

```bash
./gradlew test jacocoTestReport
```

생성된 보고서는 `build/reports/jacoco/test/html/index.html`에서 확인할 수 있습니다.

### 상세 커버리지 정보 확인

콘솔에서 클래스별 커버리지 정보를 확인하려면 다음 명령어를 실행하세요:

```bash
./gradlew jacocoDetailedReport
```

### 커버리지 검증

설정된 커버리지 기준을 충족하는지 검증하려면 다음 명령어를 실행하세요:

```bash
./gradlew jacocoTestCoverageVerification
```

### 전체 체크 실행

테스트, 커버리지 보고서 생성, 커버리지 검증을 한 번에 실행하려면:

```bash
./gradlew check
```

## API 문서(Swagger)

- 개발 환경: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- 운영/스테이징 환경: (배포 후 실제 URL 기입)
- 모든 API는 표준화된 예외 처리 및 로깅 전략을 따릅니다.
- 상세 요청/응답 구조는 Swagger 문서 또는 코드 참고.

## 예외 처리 가이드라인

프로젝트의 예외 처리는 헥사고날 아키텍처의 원칙과 객체지향 설계 원칙을 준수하여 다음과 같은 규칙을 따릅니다.

### 예외 클래스 계층 구조

```
BaseException (추상 클래스)
├── 도메인 예외
│   ├── CategoryDomainException
│   │   ├── CategoryNotFoundDomainException
│   │   ├── CategoryDuplicateNameDomainException
│   │   └── CategoryOperationException
│   ├── PromptDomainException
│   │   ├── PromptValidationException
│   │   └── PromptNotFoundException
└── 애플리케이션 예외
    ├── CategoryException
    │   ├── CategoryNotFoundException
    │   ├── CategoryDuplicateNameException
    │   └── CategoryOperationFailedException
    └── PromptException
        ├── PromptRegistrationException
        └── PromptSearchException
```

### 예외 처리 원칙

1. **계층별 책임 분리**
    - 도메인 계층: 순수한 도메인 로직과 관련된 예외 발생
    - 애플리케이션 계층: 도메인 예외를 애플리케이션 컨텍스트에 맞게 변환
    - 어댑터 계층: 기술적 예외를 도메인 예외로 변환하여 포트 명세 준수

2. **의존성 방향 준수**
    - 어댑터는 도메인 예외에만 의존 (애플리케이션 예외에 의존하지 않음)
    - 도메인 계층은 외부에 의존하지 않음
    - 애플리케이션 계층은 도메인 예외를 처리하고, 필요시 애플리케이션 예외로 변환

3. **예외 변환 유틸리티**
    - 도메인 예외를 애플리케이션 예외로 변환하는 유틸리티 클래스 사용
    - 일관된 예외 변환 패턴 적용
    - 중복 코드 제거

### 예외 발생 및 처리 예시

```java
// 어댑터 계층 (외부 → 내부)
@Override
public Category saveCategory(Category category) {
    try {
        // 외부 기술 사용
        return repository.save(entity).toDomain();
    } catch (DataAccessException e) {
        // 기술적 예외를 도메인 예외로 변환
        throw new CategoryOperationException(CategoryErrorType.PERSISTENCE_ERROR, "저장 실패", e);
    }
}

// 애플리케이션 계층
@Override
public Category createCategory(CreateCategoryCommand command) {
    try {
        // 비즈니스 로직 수행...
        return saveCategoryPort.saveCategory(category);
    } catch (CategoryDomainException e) {
        // 도메인 예외를 애플리케이션 예외로 변환
        throw CategoryExceptionConverter.convertToApplicationException(e, command.getName());
    } catch (Exception e) {
        throw new CategoryOperationFailedException("카테고리 생성 실패", e);
    }
}
```

### 예외 변환 유틸리티 패턴

```java
// 예외 변환 유틸리티
public final class CategoryExceptionConverter {

    private CategoryExceptionConverter() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }

    public static RuntimeException convertToApplicationException(CategoryDomainException e, Object identifier) {
        if (e instanceof CategoryNotFoundDomainException) {
            if (identifier instanceof Long) {
                return new CategoryNotFoundException((Long) identifier);
            } else {
                return new CategoryNotFoundException(identifier.toString());
            }
        } else if (e instanceof CategoryDuplicateNameDomainException) {
            return new CategoryDuplicateNameException(identifier.toString());
        } else {
            return new CategoryOperationFailedException(
                    "Error occurred during category operation: " + e.getMessage(), e);
        }
    }
}
```

### 로깅 전략

1. **예외 발생 위치에서 로깅**
    - 예외가 최초 발생한 위치에서 적절한 컨텍스트와 함께 로깅

2. **로그 레벨 사용**
    - ERROR: 예외 및 중요 오류 (시스템 기능 정지 수준)
    - WARN: 잠재적 문제 (기능은 작동하나 주의 필요)
    - INFO: 중요 이벤트 기록
    - DEBUG: 상세 정보 (개발/디버깅용)

3. **구조화된 예외 처리**
    - GlobalExceptionHandler를 통한 일관된 예외 응답 생성
    - ErrorResponse DTO로 클라이언트에 표준화된 오류 정보 제공

### 테스트 요구사항

각 계층의 예외 처리 로직은 다음을 테스트해야 합니다:

1. 정상 케이스 외에 예외 발생 케이스에 대한 단위 테스트 작성
2. 어댑터 계층에서는 외부 예외가 적절한 도메인 예외로 변환되는지 확인
3. 애플리케이션 계층에서는 도메인 예외를 적절히 처리하는지 확인
4. 통합 테스트에서는 예외 발생 시 클라이언트에게 적절한 응답이 전달되는지 확인

## 커버리지 설정 변경

기본적으로 코드 커버리지 기준은 최소 0%로 설정되어 있습니다.
이 기준을 높이려면 `build.gradle` 파일의 `jacocoTestCoverageVerification` 태스크에서 `minimum` 값을 수정하세요.

```groovy
jacocoTestCoverageVerification {
    violationRules {
        rule {
            element = 'CLASS'
            limit {
                counter = 'LINE'
                value = 'COVEREDRATIO'
                minimum = 0.75 // 75% 라인 커버리지 필요
            }
            // 제외할 클래스 설정
            excludes = [
                    'com.gongdel.promptserver.PromptServerApplication',
                    'com.gongdel.promptserver.config.*'
            ]
        }
    }
}
```

## 테스트 제외 설정

특정 클래스나 패키지를 테스트 커버리지 측정에서 제외하려면 `build.gradle` 파일의 `jacocoTestCoverageVerification` 태스크에서 `excludes` 목록을 수정하세요.

## Backend Server

### 개발 환경 설정

### 실행 방법

## 🛠️ 마이그레이션 체크섬 오류(Checksum mismatch) 해결

Flyway 마이그레이션 파일을 수정한 경우, 아래 명령어로 체크섬 오류를 복구할 수 있습니다.

```bash
./gradlew flywayRepair \
  -Dflyway.url=jdbc:postgresql://localhost:5432/prompt_center \
  -Dflyway.user=<DB_USER> \
  -Dflyway.password=<DB_PASSWORD>
```

- `<DB_USER>`, `<DB_PASSWORD>`는 환경에 맞게 입력하세요.
- Docker Compose 기본값은 `postgres` / `postgres` 입니다.
