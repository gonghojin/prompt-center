# 🧩 Prompth Center

**프롬프트 템플릿 중앙화 서버** - Spring Boot + Next.js 통합 배포 시스템

## 📝 프로젝트 개요
사내에서 반복적으로 사용하는 프롬프트를 역할/목적/도메인별로 등록·공유·검색·재사용할 수 있는 중앙화된 프롬프트 템플릿 서버입니다.

- **백엔드**: Spring Boot REST API
- **프론트엔드**: Next.js 웹 애플리케이션
- **배포**: Docker Compose 통합 배포 (HTTP/HTTPS 지원)

## 🏗️ 프로젝트 구조

```
prompth-center/
├── backend/                    # Spring Boot 백엔드
│   ├── src/main/java/com/promptcenter/
│   │   ├── api/               # REST API 컨트롤러
│   │   ├── application/       # 애플리케이션 서비스
│   │   ├── domain/            # 도메인 모델
│   │   ├── infrastructure/    # 인프라 계층
│   │   ├── config/            # 설정 클래스
│   │   └── common/            # 공통 유틸리티
│   ├── src/main/resources/
│   │   ├── application.yml    # 설정 파일
│   │   └── db/migration/      # Flyway 마이그레이션
│   └── build.gradle           # Gradle 설정
├── frontend/                  # Next.js 프론트엔드
│   ├── src/
│   │   ├── app/              # App Router 페이지
│   │   ├── components/       # React 컴포넌트
│   │   ├── hooks/            # 커스텀 훅
│   │   ├── services/         # API 서비스
│   │   └── styles/           # 스타일시트
│   ├── Dockerfile.production # 프로덕션 빌드용
│   ├── next.config.js        # Next.js 설정
│   └── package.json          # 의존성 관리
├── docker-compose.dev.yml     # 로컬 개발용 (분리 실행)
└── deploy/                    # 통합 배포 시스템
    ├── docker-compose.yml     # 통합 Docker Compose
    ├── config/
    │   ├── nginx/            # Nginx 설정 (라우팅)
    │   └── env/              # 환경별 설정
    ├── scripts/              # 배포 및 관리 스크립트
    └── logs/                 # 서비스별 로그
```

## 🚀 빠른 시작

### 필수 요구사항

- **Java 17+** (백엔드)
- **Node.js 18+** (프론트엔드)
- **Docker & Docker Compose** (배포)
- **Git** (소스 관리)

### 1. 프로젝트 설정

```bash
# 저장소 클론
git clone <repository-url>
cd prompth-center

# 프론트엔드 프로젝트 준비 (별도 레포지토리인 경우)
# git clone <frontend-repo> frontend
# 또는 심볼릭 링크: ln -s /path/to/frontend-project frontend
```

### 2. 환경변수 설정

```bash
# 개발환경 설정
cp deploy/config/env/dev.env.example deploy/config/env/dev.env
vim deploy/config/env/dev.env

# 운영환경 설정 (운영 배포 시)
cp deploy/config/env/prod.env.example deploy/config/env/prod.env
vim deploy/config/env/prod.env
```

### 3. 배포 실행

#### 🔧 로컬 개발 (분리 모드)
```bash
# 백엔드 실행
cd backend
./gradlew bootRun

# 프론트엔드 실행 (별도 터미널)
cd frontend
npm install
npm run dev
```

- 백엔드: http://localhost:8080
- 프론트엔드: http://localhost:3000

#### 🚀 통합 배포 (권장)
```bash
# HTTP 배포 (개발/테스트)
./deploy/scripts/deploy.sh http dev localhost

# HTTPS 배포 (운영)
./deploy/scripts/deploy.sh https prod yourdomain.com admin@yourdomain.com
```

- 웹사이트: http://localhost 또는 https://yourdomain.com
- API: /api/* 경로로 자동 라우팅

## 🔧 주요 기능

### 🌐 통합 라우팅 시스템

- **프론트엔드**: `/` → Next.js 애플리케이션
- **백엔드 API**: `/api/*` → Spring Boot REST API
- **API 문서**: `/swagger-ui.html` → Swagger UI
- **헬스체크**: `/actuator/health` → Spring Boot Actuator

### 🔒 보안 설정

- HTTPS 자동 지원 (Let's Encrypt)
- 보안 헤더 설정
- CORS 없는 동일 도메인 배포
- JWT 기반 인증

### 📊 모니터링 및 관리
```bash
# 서비스 상태 확인
docker-compose -f deploy/docker-compose.yml ps

# 통합 모니터링
./deploy/scripts/utils/monitoring.sh dev

# 로그 확인
docker-compose -f deploy/docker-compose.yml logs -f frontend
docker-compose -f deploy/docker-compose.yml logs -f app

# 백업 실행
./deploy/scripts/utils/backup.sh dev
```

## 🧪 개발 및 테스트

### 백엔드 테스트
```bash
cd backend
./gradlew test
./gradlew bootRun
```

### 프론트엔드 테스트

```bash
cd frontend
npm test
npm run build    # 프로덕션 빌드 테스트
```

### API 통합 테스트
```bash
# API 자동 테스트 (curl, jq 필요)
bash test_prompt_api.sh
```

### 통합 배포 테스트

```bash
# 로컬에서 통합 환경 테스트
./deploy/scripts/deploy.sh http dev localhost
```

## 📚 기술 스택

### 백엔드

- **Spring Boot 3.x** - 메인 프레임워크
- **Spring Data JPA** - ORM
- **Spring Security** - 보안
- **PostgreSQL** - 메인 데이터베이스
- **Redis** - 캐시 및 세션
- **Elasticsearch** - 검색 엔진
- **Flyway** - 데이터베이스 마이그레이션

### 프론트엔드

- **Next.js 14** - React 프레임워크
- **TypeScript** - 타입 안전성
- **Tailwind CSS** - 스타일링
- **React Query** - 상태 관리

### 인프라

- **Docker & Docker Compose** - 컨테이너화
- **Nginx** - 리버스 프록시
- **Let's Encrypt** - SSL 인증서

## 📖 문서 및 참고자료

### API 문서

- **개발환경**: http://localhost/swagger-ui.html
- **운영환경**: https://yourdomain.com/swagger-ui.html

### 배포 가이드

- **통합 배포 가이드**: [deploy/README.md](deploy/README.md)
- **환경별 설정**: deploy/config/env/
- **SSL 설정**: deploy/scripts/ssl/

### 아키텍처

- **백엔드 구조**: Clean Architecture + DDD
- **프론트엔드 구조**: App Router + Server Components
- **배포 구조**: 단일 도메인 통합 배포

## 🛠️ 문제 해결

### 일반적인 문제

```bash
# 포트 충돌 시
sudo lsof -i :80
sudo lsof -i :443

# 컨테이너 재시작
docker-compose -f deploy/docker-compose.yml restart

# 네트워크 정리
docker-compose -f deploy/docker-compose.yml down
docker network prune -f
```

### 빌드 문제

```bash
# 프론트엔드 의존성 재설치
cd frontend
rm -rf node_modules package-lock.json
npm install

# 백엔드 클린 빌드
cd backend
./gradlew clean build
```

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### 개발 가이드라인

- **코드 스타일**: Java (Spring Boot), TypeScript (Next.js)
- **커밋 메시지**: [Conventional Commits](https://www.conventionalcommits.org/)
- **테스트**: 기능 추가 시 테스트 코드 필수

## 📄 라이선스

이 프로젝트는 MIT 라이선스 하에 있습니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참조하세요.

---

**Prompth Center Team** | 프롬프트 템플릿 중앙화로 개발 효율성 향상
