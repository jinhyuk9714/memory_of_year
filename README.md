# Memory of Year

Memory of Year는 **멋쟁이사자처럼 12기 팀 멋삼핑에서 7인 팀 프로젝트로 진행한** 앨범, 편지, 사진, 스티커 관리 서비스의 Spring Boot REST API 백엔드입니다. JWT 인증, S3 파일 업로드, MySQL/H2 실행 환경, Swagger 문서, k6 부하 테스트 스크립트를 포함합니다.

## 프로젝트 맥락

이 저장소는 개인 토이 프로젝트가 아니라, 기획·디자인·프론트엔드·백엔드가 함께 역할을 나누어 만든 팀 프로젝트입니다. 백엔드 관점에서는 인증, 앨범/편지/사진/스티커 API, S3 연동, 성능 테스트와 N+1 개선 사례를 포트폴리오 중심으로 정리했습니다.

| 구분 | 내용 |
| --- | --- |
| 진행 과정 | 멋쟁이사자처럼 12기 |
| 팀 | 멋삼핑 |
| 팀 구성 | 7명: 디자인 1명, 프론트엔드 2명, 백엔드 4명 |
| 백엔드 범위 | REST API, JWT 인증, S3 업로드, DB 조회 최적화, k6 부하 테스트 |

## 서비스 화면

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/29880bf4-fe0b-4d6e-8473-3ffd1f1deaaf" alt="프로젝트 시작 화면" width="300"><br>
      <b>프로젝트 시작</b>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/e109d07f-ef0c-4594-ad6b-9a2661512717" alt="회원가입과 로그인 화면" width="300"><br>
      <b>회원가입 & 로그인</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/a6753028-b951-4c40-8da6-1b573d482818" alt="앨범 생성 화면" width="300"><br>
      <b>앨범 생성</b>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/17f478c4-8d78-4df3-9562-41607eef195d" alt="편지 생성 화면" width="300"><br>
      <b>편지 생성</b>
    </td>
  </tr>
</table>

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

### 부하 테스트 결과

원본 README에 남아 있던 k6 측정값 기준입니다.

| API | VU | RPS | p95 | 실패율 |
| --- | --- | --- | --- | --- |
| 회원가입 | 10 | 16.5 | 115ms | 0% |
| 로그인 | 20 | 49.7 | 116ms | 0% |
| 앨범 조회 | 30 | 143.7 | 12.9ms | 0% |
| 편지 목록 (30개) | 20 | 96.7 | 13.8ms | 0% |

### N+1 개선 기록

편지 30개와 `photoCount`를 함께 조회할 때, 사진 수 계산을 서브쿼리 기반 조회로 바꿔 쿼리 수와 응답 시간을 줄였습니다.

| 구분 | Before | After |
| --- | --- | --- |
| DB 쿼리 | 31회 (1+N) | 1회 |
| p95 | 22.8ms | 13.8ms (40% 개선) |
| 평균 | 11.5ms | 6.7ms (42% 개선) |

## 참고 사항

- 로컬 기본 설정은 S3 접근 키가 없어도 애플리케이션이 기동되도록 더미 값을 둡니다.
- 실제 S3 업로드를 사용하려면 `CLOUD_AWS_S3_BUCKET`, `CLOUD_AWS_REGION`, `CLOUD_AWS_ACCESS_KEY`, `CLOUD_AWS_SECRET_KEY`를 환경에 맞게 지정해야 합니다.
