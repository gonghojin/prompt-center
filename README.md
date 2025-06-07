# 🧩 Prompt Center

프롬프트 템플릿 중앙화 서버

## 📝 프로젝트 개요
사내에서 반복적으로 사용하는 프롬프트를 역할/목적/도메인별로 등록·공유·검색·재사용할 수 있는 중앙화된 프롬프트 템플릿 서버입니다.

## 🚀 시작하기

### 필수 요구사항
- Java 17+
- Node.js 18+
- Docker & Docker Compose
- PostgreSQL 15+
- Redis 7+
- Elasticsearch 8+
- Gradle 8.5+

### 개발 환경 설정

1. 저장소 클론
```bash
git clone <repository-url>
cd prompt-center
```

2. 백엔드 설정
```bash
cd backend

# Gradle Wrapper가 없는 경우 생성
gradle wrapper

# 프로젝트 빌드
./gradlew build

# 개발 서버 실행
./gradlew bootRun
```

3. 프론트엔드 설정
```bash
cd frontend
npm install
```

4. 환경 변수 설정
```bash
# backend/src/main/resources/application.yml
cp backend/src/main/resources/application.yml.example backend/src/main/resources/application.yml
# frontend/.env
cp frontend/.env.example frontend/.env
```

5. Docker로 실행
```bash
# 전체 서비스 실행
docker-compose up -d

# 개별 서비스 실행
docker-compose up -d backend
docker-compose up -d frontend
```

## 🏗️ 프로젝트 구조
```
prompt-center/
├── backend/                 # Spring Boot 백엔드
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/promptcenter/
│   │   │   │       ├── api/            # API 컨트롤러
│   │   │   │       ├── config/         # 설정
│   │   │   │       ├── domain/         # 도메인 모델
│   │   │   │       ├── infrastructure/ # 인프라 계층
│   │   │   │       ├── application/    # 애플리케이션 서비스
│   │   │   │       └── common/         # 공통 유틸리티
│   │   │   └── resources/
│   │   │       ├── application.yml     # 설정 파일
│   │   │       └── db/migration/       # Flyway 마이그레이션
│   │   └── test/                       # 테스트
│   ├── build.gradle                    # Gradle 설정
│   ├── settings.gradle                 # Gradle 프로젝트 설정
│   ├── gradlew                         # Gradle Wrapper 실행 스크립트
│   └── gradle/                         # Gradle Wrapper 설정
├── frontend/               # Next.js 프론트엔드
│   ├── src/
│   │   ├── app/           # 페이지
│   │   ├── components/    # UI 컴포넌트
│   │   ├── hooks/         # 커스텀 훅
│   │   ├── services/      # API 서비스
│   │   └── styles/        # 스타일
│   └── package.json
└── docker/                # Docker 설정
```

## 🧪 테스트
```bash
# 백엔드 테스트
cd backend
./gradlew test

```

## 🧩 API 통합 테스트 자동화

`test_prompt_api.sh` 스크립트로 카테고리/프롬프트 API의 통합 테스트를 자동화할 수 있습니다.

- curl, jq가 필요합니다 (macOS: `brew install jq`)
- 백엔드 서버가 기동된 상태에서 실행하세요.

```bash
bash test_prompt_api.sh
```

## 📚 문서
- [API 문서](http://localhost:8080/swagger-ui.html)
- [아키텍처 문서](req/architecture.md)
- [데이터 모델](req/model.md)

## 🤝 기여하기
1. Fork the Project
2. Create your Feature Branch
3. Commit your Changes
4. Push to the Branch
5. Open a Pull Request

## 📄 라이선스
이 프로젝트는 MIT 라이선스 하에 있습니다.
