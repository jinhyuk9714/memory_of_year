# Memory of Year (백엔드)

앨범, 사진, 편지, 스티커 등 사용자 생성 콘텐츠를 관리하는 백엔드 애플리케이션입니다.

**멋쟁이사자처럼 12기** 팀 **멋삼핑**에서 제작했습니다.  
디자인 1명, 프론트엔드 2명, 백엔드 4명이 참여했습니다.

---

## 🚀 주요 기능

- **JWT 토큰**을 활용한 사용자 인증·로그아웃(토큰 무효화) 및 권한 관리
- **앨범** 생성·조회·수정 (제목, 색상, 공개 여부, 스티커 URL)
- **편지** 작성·목록 조회·상세 조회 (익명/작성자명, 색상 지원)
- **사진** 업로드·목록·상세 조회 (S3 저장, 코멘트·스티커 URL)
- **스티커** 목록 조회 (S3 `stickers/` 폴더 기반)
- **홈** 앨범별 내 앨범/타인 앨범 구분 및 액션 정보
- **공통 API 응답** `{ success, data, error }` 형식 및 전역 예외 처리
- **Swagger**를 통한 API 문서화 및 테스트
- **AWS S3** 미디어 저장, **MySQL** 데이터베이스, **AWS EC2** 호스팅·**IAM** 접근 제어

---

## 🛠️ 사용 기술

| 기술 | 설명 |
|------|------|
| **Spring Boot 3** | REST API 및 비즈니스 로직 |
| **Spring Security + JWT** | 인증·인가, 토큰 검증·블랙리스트 |
| **Spring Data JPA** | 엔티티·리포지토리 |
| **MySQL** | 관계형 DB |
| **AWS S3** | 사진·스티커 파일 저장 |
| **Swagger (springdoc-openapi)** | API 문서·UI |
| **Lombok** | 보일러플레이트 감소 |
| **Gradle** | 빌드·의존성 관리 |

---

## 🏗️ 아키텍처

### 시스템 구성

```mermaid
flowchart LR
    subgraph Client["클라이언트"]
        FE[프론트엔드]
    end

    subgraph Backend["Spring Boot (EC2)"]
        API[REST API]
        JWT[JWT 검증]
        API --> JWT
    end

    subgraph Data["데이터·스토리지"]
        MySQL[(MySQL)]
        S3[(AWS S3)]
    end

    FE -->|"HTTPS"| API
    JWT --> MySQL
    API --> MySQL
    API --> S3
```

- 클라이언트(프론트엔드)가 REST API로 요청 → JWT 검증 후 Controller → Service → MySQL/S3 사용.

### 레이어드 아키텍처 (백엔드)

```mermaid
flowchart TB
    subgraph Presentation["Presentation Layer"]
        Controller[Controller]
    end

    subgraph Business["Business Layer"]
        Service[Service]
    end

    subgraph DataAccess["Data Access Layer"]
        Repository[Repository]
    end

    subgraph External["External"]
        DB[(MySQL)]
        S3[(AWS S3)]
    end

    Controller --> Service
    Service --> Repository
    Service --> S3
    Repository --> DB
```

| 레이어 | 역할 |
|--------|------|
| **Controller** | HTTP 요청/응답, DTO 변환, 인증 정보 활용 |
| **Service** | 비즈니스 로직, 트랜잭션, 예외 발생 |
| **Repository** | JPA로 엔티티 CRUD |
| **Filter** | JWT 검증·블랙리스트 확인 후 SecurityContext 설정 |

---

## 📐 ERD (Entity Relationship Diagram)

```mermaid
erDiagram
    user ||--o{ album : "소유(owner)"
    album ||--o{ letter : "포함"
    letter ||--o{ photo : "포함"

    user {
        bigint user_id PK "사용자 ID"
        varchar username UK "로그인 ID"
        varchar password "암호화 비밀번호"
        varchar nickname UK "닉네임"
        varchar email UK "이메일"
        varchar role "역할(USER 등)"
    }

    album {
        bigint album_id PK "앨범 ID"
        varchar title "제목"
        varchar album_color "앨범 색상"
        boolean visibility "공개 여부"
        varchar sticker_url "대표 스티커 URL"
        bigint user_id FK "소유자"
    }

    letter {
        bigint letter_id PK "편지 ID"
        varchar letter_title "편지 제목"
        varchar author "작성자명"
        text content "내용"
        boolean is_anonymous "익명 여부"
        varchar letter_color "편지 색상"
        datetime created_at "작성 시각"
        bigint album_id FK "소속 앨범"
    }

    photo {
        bigint photo_id PK "사진 ID"
        varchar url "이미지 URL(S3)"
        varchar comment "코멘트"
        varchar sticker_url "스티커 URL"
        bigint letter_id FK "소속 편지"
    }

    sticker {
        bigint id PK "스티커 ID"
        varchar file_name UK "파일명"
        varchar file_url "S3 URL"
        datetime created_at "등록 시각"
    }
```

- **user** : 회원 정보. 한 명이 여러 **album**을 소유할 수 있음(실제 서비스에서는 1인 1앨범 정책 적용 가능).
- **album** : 앨범. 여러 **letter**를 가짐.
- **letter** : 편지. 여러 **photo**를 가짐.
- **photo** : 사진. S3 URL·코멘트·스티커 URL 저장.
- **sticker** : 스티커 마스터. 앨범·사진의 `sticker_url`은 문자열로 저장되며, 스티커 목록은 S3 폴더와 연동해 사용.

---

## 📊 시퀀스 다이어그램

### 로그인

```mermaid
sequenceDiagram
    participant C as 클라이언트
    participant API as UserController
    participant US as UserService
    participant UR as UserRepository
    participant JWT as JwtTokenProvider

    C->>+API: POST /api/auth/login (username, password)
    API->>+US: authenticateUser(username, password)
    US->>UR: findByUsername(username)
    UR-->>US: Optional<User>
    US->>US: passwordEncoder.matches()
    US-->>-API: Optional<User>
    alt 인증 성공
        API->>JWT: createToken(username)
        JWT-->>API: token
        API-->>C: 200 { success, data: { token } }
    else 인증 실패
        API-->>C: 401 { success: false, error }
    end
```

### 앨범 생성

```mermaid
sequenceDiagram
    participant C as 클라이언트
    participant API as AlbumController
    participant US as UserService
    participant AS as AlbumService
    participant SS as StickerService
    participant AR as AlbumRepository

    C->>+API: POST /api/albums/create (Authorization, body)
    API->>US: findByUsername(username)
    US-->>API: User
    API->>AS: hasAlbum(user)
    AS->>AR: findByOwner(user)
    AR-->>AS: Optional
    AS-->>API: boolean
    API->>SS: isValidStickerUrl(stickerUrl)
    SS-->>API: boolean
    API->>AS: createAlbum(...)
    AS->>AR: save(album)
    AR-->>AS: Album
    AS-->>API: Album
    API-->>C: 201 { success, data: Album }
```

### 편지 작성

```mermaid
sequenceDiagram
    participant C as 클라이언트
    participant API as LetterController
    participant LS as LetterService
    participant AR as AlbumRepository
    participant LR as LetterRepository

    C->>+API: POST /api/albums/{albumId}/create (body)
    API->>+LS: createLetter(albumId, LetterRequestDto)
    LS->>AR: findById(albumId)
    AR-->>LS: Album
    LS->>LS: Letter.builder()...build()
    LS->>LR: save(letter)
    LR-->>LS: Letter
    LS-->>-API: Letter
    API-->>C: 201 { success, data: LetterListResponseDto }
```

### 사진 업로드

```mermaid
sequenceDiagram
    participant C as 클라이언트
    participant API as PhotoController
    participant PS as PhotoService
    participant LR as LetterRepository
    participant S3 as S3Service
    participant PR as PhotoRepository

    C->>+API: POST /api/letters/{letterId}/photos (multipart)
    API->>+PS: addPhoto(letterId, file, comment, stickerUrl)
    PS->>LR: findById(letterId)
    LR-->>PS: Letter
    PS->>S3: uploadFile(file)
    S3->>S3: S3 putObject
    S3-->>PS: fileUrl
    PS->>PS: Photo.builder()...build()
    PS->>PR: save(photo)
    PR-->>PS: Photo
    PS-->>-API: Photo
    API-->>C: 201 { success, data: PhotoResponseDto }
```

---

## 📁 프로젝트 구조 (백엔드)

```
src/main/java/com/demo/album/
├── config/          # Security, S3, Swagger, Web, App 설정
├── controller/      # REST API (auth, albums, letters, photos, stickers, home)
├── dto/             # 요청·응답 DTO, ApiResponse
├── entity/          # User, Album, Letter, Photo, Sticker
├── exception/       # 커스텀 예외, GlobalExceptionHandler
├── filter/          # JwtTokenFilter
├── repository/      # JPA Repository
├── service/         # 비즈니스 로직 (User, Album, Letter, Photo, Sticker, S3)
└── util/            # JwtTokenProvider
```

---

## 🏃 실행 방법

- **Java 17**, **Gradle** 필요
- `application.yml`(또는 `application.properties`)에 MySQL, AWS S3 설정

```bash
./gradlew bootRun
```

- API 문서: `http://localhost:8080/swagger-ui.html` (실행 후)

---

## 📽️ 프로젝트 데모 (GIF)

<table>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/29880bf4-fe0b-4d6e-8473-3ffd1f1deaaf" alt="앨범 생성" width="300"><br>
      <b>프로젝트 시작</b>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/e109d07f-ef0c-4594-ad6b-9a2661512717" alt="이미지 업로드" width="300"><br>
      <b>회원가입 & 로그인</b>
    </td>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/a6753028-b951-4c40-8da6-1b573d482818" alt="편지 작성" width="300"><br>
      <b>앨범 생성</b>
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/17f478c4-8d78-4df3-9562-41607eef195d" alt="스티커 관리" width="300"><br>
      <b>편지 생성</b>
    </td>
  </tr>
</table>
