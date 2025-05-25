# 🎨 프론트엔드 설계 - 프롬프트 템플릿 중앙화 서버

## 🧭 기본 원칙 (Design Principles)
- **도메인 중심 UI 설계**: 역할/카테고리 기반 탐색에 최적화
- **컴포넌트 기반 구조**: 재사용성 높은 UI 컴포넌트 분리
- **상태관리 최소화**: React Query를 통한 서버 상태 중심 관리
- **접근성과 반응성 고려**: Tailwind 기반 반응형 설계 + 키보드 접근성

---

## 💡 프론트엔드 시스템 아키텍처

### 🔧 기술 스택
- **언어**: TypeScript
- **프레임워크**: Next.js (App Router 기반)
- **라이브러리**:
  - React
  - TailwindCSS
  - React Query / SWR
  - React Hook Form
  - Zod (타입 검증)

---

## 📁 실제 프로젝트 폴더 구조 (2024.06 기준)
```
/frontend
│
├── src/
│   ├── api/                # API 클라이언트 및 인터페이스
│   │   └── prompts.ts
│   │
│   ├── app/                # Next.js 13+ App Router
│   │   └── ...
│   │
│   ├── components/         # 재사용 가능한 컴포넌트
│   │   ├── features/      # 주요 도메인별 컴포넌트 (PromptCard, PromptDetail 등)
│   │   ├── forms/         # 폼 관련 컴포넌트
│   │   └── prompt/        # 프롬프트 리스트 등
│   │
│   ├── hooks/              # 커스텀 훅 (useAuth, usePrompt 등)
│   │
│   ├── lib/                # 유틸리티 함수 (api.ts 등)
│   │
│   ├── mocks/              # 목업 데이터 (mockPrompts.ts 등)
│   │
│   ├── types/              # TypeScript 타입 정의 (index.ts, prompt.ts 등)
│   │
│   └── styles/             # 전역 스타일
│
├── public/                 # 정적 파일 (현재 없음, 필요시 생성)
│   ├── images/
│   └── fonts/
│
├── tailwind.config.js      # Tailwind 설정
├── next.config.js          # Next.js 설정
└── package.json            # 의존성 관리
```

---

## 🔹 주요 화면 & 기능 매핑

### 1. 📄 프롬프트 목록 화면 (`/`)
- 기능: 키워드/태그 검색, 리스트/카드 뷰 전환, 정렬
- 구성: `PromptSearchInput`, `PromptCard[]`, `Pagination`
- 상태관리: `useQuery`(`GET /prompts?keyword=...`)

### 2. 🔍 프롬프트 상세 페이지 (`/prompt/[id]`)
- 기능: 본문, 예시, 역할, 태그, 작성자, 버전 보기
- 구성: `PromptDetail`, `VersionHistory`, `Edit/Delete` 버튼
- 상태관리: `useQuery`(`GET /prompts/{id}`)

### 3. ➕ 프롬프트 등록/수정 (`/prompt/new` 또는 `/prompt/edit/[id]`)
- 기능: 역할/카테고리/태그 선택, 예시 입력, 템플릿 본문 작성
- 구성: `PromptForm`, `InputList`, `SelectBox`
- 상태관리: `useMutation`(`POST /prompts`, `PUT /prompts/{id}`)

### 4. 📁 마이 프롬프트/즐겨찾기 (`/my`, `/favorites`)
- 기능: 내가 만든 템플릿, 즐겨찾기한 템플릿 목록 조회
- 구성: `PromptCard[]`, 탭/필터
- 상태관리: `useQuery`(`GET /users/me/prompts`, `GET /users/me/favorites`)

### 5. 🔒 팀/공개 범위 설정
- 기능: 등록 시 공개 범위 선택 (전체/팀/개인)
- 구현: `SelectBox`, 서버로 `visibility` 필드 전송

---

## ⚙️ 상태 관리 전략
- **React Query / SWR (서버 상태)**
  - 프롬프트 목록, 상세, 즐겨찾기 등 캐싱 및 요청
- **React Hook Form + Zod (폼 상태 및 유효성)**
- **Local State (UI 상태)**
  - 검색어, 정렬, 현재 페이지 등
- **Session 기반 인증 상태**
  - 로그인 세션은 쿠키/Redis 기반 처리

---

## 🎨 스타일 가이드 (Tailwind)
- 버튼: `rounded-2xl px-4 py-2 shadow hover:bg-gray-100`
- 카드: `rounded-xl border p-4 hover:shadow-lg`
- 입력창: `w-full border rounded-md px-3 py-2 focus:outline-none`

---

## 🧪 테스트 전략
- **단위 테스트**
  - `Jest` + `React Testing Library`
  - 컴포넌트 단위 테스트 중심
- **E2E 테스트**
  - `Cypress`
  - 사용자 시나리오 기반 자동화 테스트
- **자동화 도구**
  - GitHub Actions: 빌드/테스트 자동화
  - Husky: pre-commit lint/test 실행

---

## 🚀 성능 최적화 전략
- 이미지 최적화 (Next/Image)
- 코드 스플리팅
- SSR (서버 사이드 렌더링)
- SSG (정적 사이트 생성)
- 캐싱 전략 (`Cache-Control`, React Query 캐시)
- 번들 사이즈 최적화 (`next/bundle-analyzer` 활용 가능)

---

## 🔐 접근 제어
- 인증 사용자만 등록/수정 가능
- 역할(Role)에 따른 UI 노출 및 API 권한 체크
  - 예: 팀 관리자만 공유 설정 변경 가능

---

## 📌 향후 확장 고려
- 태그 추천 자동완성
- 팀 단위 템플릿 그룹핑 및 대시보드
- 프롬프트 사용 횟수 및 인기순 정렬

---

## ✅ 체크리스트
- [ ] 페이지별 컴포넌트 완성
- [ ] API 연동 점검
- [ ] Tailwind 스타일 통일성 확인
- [ ] 권한 제어 테스트
- [ ] 반응형 & 다크모드 대응
