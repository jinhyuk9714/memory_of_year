# 부하 테스트 (k6)

포트폴리오용 성능 테스트 스크립트입니다.

## k6 설치

```bash
# macOS (Homebrew)
brew install k6

# 또는 공식 사이트
# https://k6.io/docs/getting-started/installation/
```

## 실행 방법

### 1. 서버 기동

```bash
./gradlew bootRun
```

### 2. 스모크 테스트 (전체 플로우 1회)

```bash
k6 run load-test/00-smoke.js
```

### 3. 회원가입 부하 테스트

```bash
k6 run load-test/01-register.js
```

옵션 예: 동시 50명, 1분간

```bash
k6 run --vus 50 --duration 1m load-test/01-register.js
```

### 4. 로그인 부하 테스트

사전에 테스트 계정 생성:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"loadtest","password":"pass123","nickname":"테스트","email":"load@test.com"}'
```

실행:

```bash
k6 run load-test/02-login.js
```

### 5. 앨범 조회 부하 테스트

```bash
k6 run load-test/03-album-get.js   # setup에서 자동으로 회원가입·로그인·앨범 생성
```

### 6. 편지 목록 부하 테스트 (N+1 Before/After 비교용)

편지 30개 앨범을 만들어 편지 목록 조회 부하를 측정합니다.

```bash
# After (최적화 버전, 기본)
k6 run load-test/04-letters.js

# Before (N+1 버전) - 서버를 N+1 모드로 재시작 후 실행
APP_PERF_USE_N1_LETTERS=true ./gradlew bootRun
# 다른 터미널에서:
k6 run load-test/04-letters.js
```

## HTML 리포트 생성

```bash
k6 run --out json=result.json load-test/01-register.js
# 또는
k6 run --out html=report.html load-test/01-register.js
```

## 포트폴리오용 메트릭

- **http_req_duration**: 응답 시간 (평균, p95, p99)
- **http_reqs**: 초당 요청 수 (RPS)
- **http_req_failed**: 실패율
- **iterations**: 총 반복 횟수
