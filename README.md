# Memory of Year

Memory of Year는 앨범, 편지, 사진, 스티커를 관리하는 Spring Boot REST API 백엔드입니다. JWT 인증, S3 파일 업로드, MySQL/H2 실행 환경, Swagger 문서, k6 부하 테스트 스크립트를 포함합니다.

## 문제 의식

추억을 앨범 단위로 모으는 서비스에서는 사용자 인증, 미디어 업로드, 앨범별 편지/사진 조회가 함께 동작해야 합니다. 이 저장소는 그 흐름을 REST API로 나누고, 목록 조회에서 발생할 수 있는 N+1 쿼리를 줄이는 방식까지 함께 정리합니다.

## 주요 기능

- 회원가입, 로그인, 로그아웃과 JWT 필터 기반 인증
- 앨범 생성, 조회, 수정
- 앨범 안의 편지 작성, 목록, 상세 조회
- 사진 업로드와 조회, 스티커 업로드와 목록 조회
- AWS S3 연동 설정과 로컬 기본값
- 공통 응답 형식과 전역 예외 처리
- Swagger UI, Actuator health/metrics/info
- k6 기반 회원가입, 로그인, 앨범 조회, 편지 목록 부하 테스트

## 기술 스택

| 영역 | 기술 |
| --- | --- |
| Backend | Java 17, Spring Boot 3.3.5, Spring Web, Spring Security |
| Persistence | Spring Data JPA, MySQL 8, H2 |
| Storage | AWS SDK S3 |
| API Docs | springdoc-openapi |
| Test / Quality | JUnit 5, Spring Security Test, JaCoCo, k6 |
| Infra | Docker, Docker Compose, Gradle |

## 구조

```text
src/main/java/com/demo/album/
├── config/       # Security, Swagger, S3, Web 설정
├── controller/   # User, Album, Letter, Photo, Sticker API
├── dto/          # 요청/응답 DTO와 공통 응답
├── entity/       # User, Album, Letter, Photo, Sticker
├── exception/    # 전역 예외 처리
├── filter/       # JWT 필터
├── repository/   # JPA Repository
├── service/      # 비즈니스 로직
└── util/         # JwtTokenProvider
```

## 실행 방법

기본 프로필은 H2 인메모리 DB를 사용합니다.

```bash
./gradlew bootRun
```

MySQL 프로필로 실행하려면 Docker Compose를 사용하거나 MySQL 환경 변수를 설정합니다.

```bash
docker compose up --build
```

```bash
./gradlew bootRun --args='--spring.profiles.active=mysql'
```

H2 전용 Compose 파일도 포함되어 있습니다.

```bash
docker compose -f docker-compose.h2.yml up --build
```

테스트와 커버리지 리포트는 Gradle로 실행합니다.

```bash
./gradlew test
./gradlew test jacocoTestReport
```

실행 후 API 문서는 `http://localhost:8080/swagger-ui.html`에서 확인할 수 있습니다.

## 성능 테스트

`load-test/`에는 다음 k6 스크립트가 포함되어 있습니다.

```bash
k6 run load-test/01-register.js
k6 run load-test/02-login.js
k6 run load-test/03-album-get.js
k6 run load-test/04-letters.js
```

기존 README와 `load-test/README.md`에는 편지 목록 조회의 photo count 계산을 서브쿼리 기반 조회로 줄인 비교 기록이 정리되어 있습니다.

## 참고 사항

- 로컬 기본 설정은 S3 접근 키가 없어도 애플리케이션이 기동되도록 더미 값을 둡니다.
- 실제 S3 업로드를 사용하려면 `CLOUD_AWS_S3_BUCKET`, `CLOUD_AWS_REGION`, `CLOUD_AWS_ACCESS_KEY`, `CLOUD_AWS_SECRET_KEY`를 환경에 맞게 지정해야 합니다.
