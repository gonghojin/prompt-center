# Prompth Center 통합 배포 가이드

Spring Boot 백엔드와 Next.js 프론트엔드를 통합하여 배포하는 시스템입니다.

## 📁 프로젝트 구조

```
prompth-center/
├── backend/                    # Spring Boot 백엔드
├── frontend/                   # Next.js 프론트엔드
├── docker-compose.dev.yml      # 개발용 (분리 실행)
└── deploy/                     # 통합 배포 설정
    ├── docker-compose.yml      # 통합 Docker Compose
    ├── config/
    │   ├── nginx/              # Nginx 설정 (통합 라우팅)
    │   └── env/                # 환경별 설정
    ├── scripts/                # 배포 및 관리 스크립트
    └── logs/                   # 서비스별 로그
```

## 🎯 배포 모드

### 1. 통합 배포 (권장)

- **장점**: 단일 도메인, CORS 없음, 간단한 운영
- **구조**: Nginx → Frontend (/) + Backend (/api)
- **용도**: 프로덕션 환경

### 2. 분리 개발

- **장점**: 독립적 개발, HMR 지원
- **구조**: Frontend:3000, Backend:8080
- **용도**: 로컬 개발

## 🚀 빠른 시작

### 사전 준비

#### 1. 프론트엔드 프로젝트 준비

```bash
# 프론트엔드 프로젝트를 frontend/ 디렉토리에 위치
git clone <frontend-repo> frontend

# 또는 심볼릭 링크 생성
ln -s /path/to/frontend-project frontend
```

#### 2. 환경변수 설정

```bash
# 개발환경
cp deploy/config/env/dev.env.example deploy/config/env/dev.env
vim deploy/config/env/dev.env

# 운영환경
cp deploy/config/env/prod.env.example deploy/config/env/prod.env
vim deploy/config/env/prod.env
```

### 배포 실행

#### HTTP 배포 (개발)

```bash
./deploy/scripts/deploy.sh http dev localhost
```

#### HTTPS 배포 (운영)

```bash
./deploy/scripts/deploy.sh https prod yourdomain.com admin@yourdomain.com
```

## 📋 주요 명령어

### 배포 관리

```bash
# 통합 배포
./deploy/scripts/deploy.sh [http|https] [dev|prod] [domain] [email]

# 서비스 상태 확인
docker-compose -f deploy/docker-compose.yml ps

# 서비스 재시작
docker-compose -f deploy/docker-compose.yml restart [service]

# 서비스 중지
docker-compose -f deploy/docker-compose.yml down
```

### 로그 관리

```bash
# 통합 로그
docker-compose -f deploy/docker-compose.yml logs -f

# 개별 서비스 로그
docker-compose -f deploy/docker-compose.yml logs -f frontend
docker-compose -f deploy/docker-compose.yml logs -f app
docker-compose -f deploy/docker-compose.yml logs -f nginx
```

### 모니터링

```bash
# 통합 모니터링
./deploy/scripts/utils/monitoring.sh dev

# 리소스 사용량
docker stats
```

## 🔧 서비스 구성

### 핵심 서비스

1. **Nginx** (포트 80, 443)
    - 리버스 프록시 및 로드 밸런서
    - SSL 종료 처리
    - 정적 파일 캐싱

2. **Frontend** (내부 포트 3000)
    - Next.js 애플리케이션
    - SSR/SSG 지원
    - 프론트엔드 라우팅

3. **Backend** (내부 포트 8080)
    - Spring Boot REST API
    - 비즈니스 로직 처리
    - 데이터베이스 연동

4. **PostgreSQL**
    - 메인 데이터베이스
    - 트랜잭션 데이터 저장

5. **Redis**
    - 캐시 및 세션 스토어
    - 임시 데이터 저장

6. **Elasticsearch**
    - 검색 엔진
    - 로그 분석

### 라우팅 구조

```
https://yourdomain.com/
├── /                          → Frontend (Next.js)
├── /dashboard                 → Frontend
├── /api/*                     → Backend (Spring Boot)
├── /actuator/*                → Backend (Health Check)
├── /swagger-ui/*              → Backend (API 문서)
├── /_next/*                   → Frontend (정적 파일)
└── /static/*                  → Frontend (assets)
```

## 🔒 보안 설정

### SSL/HTTPS

- **개발환경**: 자체 서명 인증서
- **운영환경**: Let's Encrypt 자동 갱신

### 보안 헤더

```nginx
X-Frame-Options: SAMEORIGIN
X-Content-Type-Options: nosniff
X-XSS-Protection: 1; mode=block
Strict-Transport-Security: max-age=31536000
Referrer-Policy: strict-origin-when-cross-origin
```

### CORS 설정

통합 배포에서는 동일 도메인이므로 CORS 불필요

## 📊 성능 최적화

### Nginx 최적화

- Gzip 압축 활성화
- 정적 파일 캐싱 (1년)
- Connection pooling
- 업스트림 keepalive

### 프론트엔드 최적화

- Next.js Standalone 빌드
- 이미지 최적화 (WebP, AVIF)
- 코드 분할 및 지연 로딩
- 번들 크기 최적화

### 백엔드 최적화

- Connection pooling
- JPA 최적화
- Redis 캐싱
- 적절한 JVM 튜닝

## 🔍 모니터링

### 헬스체크 엔드포인트

- **전체**: `GET /actuator/health`
- **프론트엔드**: `GET /frontend-health`
- **백엔드**: `GET /actuator/health`

### 로그 위치

```
deploy/logs/
├── nginx/                     # Nginx 로그
├── app/                       # Spring Boot 로그
└── frontend/                  # Next.js 로그
```

### 메트릭 수집

- Spring Boot Actuator
- Docker 메트릭
- Nginx 상태 모니터링

## 🗄️ 백업 및 복구

### 자동 백업 설정

```bash
# cron 설정
./deploy/scripts/utils/setup-cron.sh dev

# 수동 백업
./deploy/scripts/utils/backup.sh dev
```

### 백업 대상

- PostgreSQL 데이터베이스
- Redis 데이터
- 프론트엔드 빌드 파일 (선택적)

### 보관 정책

- 7일 보관 (자동 삭제)
- 압축 저장 (gzip)

## 🔧 개발 환경 설정

### 로컬 개발 (분리 모드)

```bash
# 백엔드 실행
cd backend
./gradlew bootRun

# 프론트엔드 실행 (별도 터미널)
cd frontend
npm run dev
```

### 통합 개발 테스트

```bash
# 통합 환경에서 테스트
./deploy/scripts/deploy.sh http dev localhost
```

## 📱 환경변수 설정

### 프론트엔드 환경변수

```bash
# 백엔드 통합 모드
NEXT_PUBLIC_BACKEND_INTEGRATED=true
NEXT_PUBLIC_API_URL=/api

# 분리 모드
NEXT_PUBLIC_BACKEND_INTEGRATED=false
NEXT_PUBLIC_API_URL=http://localhost:8080/api
```

### 백엔드 환경변수

```bash
# 데이터베이스
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/${DB_NAME}
SPRING_DATASOURCE_USERNAME=${DB_USERNAME}
SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD}

# 실제 환경변수 설정
DB_NAME=prompth_center_dev
DB_USERNAME=prompth_user
DB_PASSWORD=your_password

# Redis
SPRING_DATA_REDIS_HOST=redis
SPRING_DATA_REDIS_PASSWORD=${REDIS_PASSWORD}
REDIS_PASSWORD=your_redis_password

# 보안
JWT_SECRET=your_jwt_secret
```

## 🚧 문제 해결

### 프론트엔드 빌드 실패

```bash
# 의존성 재설치
cd frontend
rm -rf node_modules package-lock.json
npm install

# 빌드 테스트
npm run build
```

### 네트워크 연결 문제

```bash
# 컨테이너 간 통신 확인
docker-compose exec nginx ping frontend
docker-compose exec frontend ping app

# 네트워크 재생성
docker-compose down
docker network prune -f
docker-compose up -d
```

### SSL 인증서 문제

```bash
# 인증서 재생성
./deploy/scripts/ssl/generate.sh [dev|prod] [domain] [email]

# 인증서 권한 확인
ls -la deploy/config/nginx/ssl/
```

### 포트 충돌

```bash
# 포트 사용 확인
sudo lsof -i :80
sudo lsof -i :443

# 프로세스 종료
sudo kill -9 [PID]
```

## 📞 지원 및 문제 신고

문제 발생 시 다음 정보를 수집하여 보고하세요:

1. **환경 정보**: OS, Docker 버전
2. **에러 로그**: `docker-compose logs`
3. **컨테이너 상태**: `docker-compose ps`
4. **시스템 리소스**: `docker stats`

---

**Prompth Center Team** | [GitHub](https://github.com/your-org/prompth-center)
