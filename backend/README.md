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
