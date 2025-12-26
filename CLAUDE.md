# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SafeLens backend is a Spring Boot application that manages image privacy filtering. The application tracks user image processing history and detects sensitive information (QR codes, text, location, faces) in images, applying filters (blur, mosaic, AI) to protect privacy.

## Tech Stack

- **Framework**: Spring Boot 4.0.1
- **Java Version**: 17
- **Build Tool**: Gradle
- **Database**: MySQL 8.4
- **ORM**: Spring Data JPA with Hibernate
- **Key Libraries**: Lombok, Spring Boot DevTools

## Development Commands

### Database Setup
```bash
# Start MySQL database with Docker Compose
docker compose up -d db

# Stop database
docker compose down
```

The database runs on port `13306` (mapped from container's 3306).

### Build & Run
```bash
# Build the project
./gradlew build

# Run the application
./gradlew bootRun

# Clean build
./gradlew clean build
```

The application runs on port `8080` by default.

### Testing
```bash
# Run all tests
./gradlew test

# Run tests with JUnit Platform
./gradlew test --tests "ClassName"

# Run a single test method
./gradlew test --tests "ClassName.methodName"
```

## Architecture

### System Architecture

```
[Client]
  ↓
[Backend Server (Spring Boot)] ← → [MySQL DB]
  ↓
[Image Server] ← → [Vertex AI (Gemini + Imagen)]
```

- **Backend Server**: 클라이언트 요청 처리, DB 관리, Image Server 중계
- **Image Server**: AI 기반 개인정보 감지 및 이미지 편집 (현재 Mock 구현)
- **MySQL DB**: 사용자 정보 및 편집 히스토리 관리

### Implemented APIs

1. **POST /detect** - 개인정보 감지 API
   - Image Server로 요청 중계 (현재 Mock)
   - DB 저장 없음 (단순 중계)

2. **POST /edit** - 이미지 편집 API
   - Image Server로 편집 요청 (현재 Mock)
   - History 및 Detect 정보 DB 저장
   - 편집된 이미지 URL 반환

3. **GET /history/{memberId}** - 히스토리 조회 API
   - 특정 사용자의 전체 편집 히스토리 조회
   - 최신순 정렬

### Domain Model

The application follows a three-entity relational model:

**Member** (User)
- Represents users of the system
- Has a one-to-many relationship with History
- Uses JPA Auditing for automatic timestamp management (`createdAt`, `updatedAt`)

**History** (Image Processing Record)
- Central entity tracking each image processing operation
- Stores original (`oldUuid`) and processed (`newUuid`) image identifiers
- Tracks the filter type applied: `BLUR`, `MOSAIC`, or `AI`
- Belongs to a Member (many-to-one)
- Has many Detect entries (one-to-many)
- Uses JPA Auditing for `createdAt` timestamp
- Uses `@Builder` pattern for entity creation

**Detect** (Sensitive Information Detection)
- Records detected sensitive areas in images
- Stores bounding box coordinates (`x`, `y`, `width`, `height`)
- Categories: `QRBARCODE`, `TEXT`, `LOCATION`, `FACE`, `ETC`
- Belongs to a History (many-to-one)
- Uses `@Builder` pattern for entity creation

### Package Structure

```
safelens.backend/
├── config/         # Configuration classes (JPA Auditing, RestTemplate)
├── domain/         # JPA entities (Member, History, Detect)
├── dto/            # Data Transfer Objects
├── controller/     # REST API controllers
├── service/        # Business logic layer
├── repository/     # JPA repositories
└── util/           # Utility classes (ImageUrlUtil)
```

### Database Configuration

- **Hibernate DDL**: `validate` mode (validates schema without modifications)
- **Connection**: Local development uses `localhost:13306`, container uses `db:3306`
- **Timezone**: Asia/Seoul
- **Charset**: utf8mb4 with unicode collation
- **Open-in-view**: Disabled (explicit transaction boundaries required)

### Key Design Patterns

- **Lombok**: All entities and DTOs use `@Getter`, `@NoArgsConstructor(access = AccessLevel.PROTECTED)`, `@AllArgsConstructor`
- **Builder Pattern**: Domain entities (History, Detect) use `@Builder` for object creation
- **JPA Auditing**: Enabled via `AuditingConfig` for automatic timestamp management with `@CreatedDate` and `@LastModifiedDate`
- **Lazy Loading**: Foreign key relationships use `FetchType.LAZY` to avoid N+1 queries
- **Cascade Operations**: Parent entities use `CascadeType.ALL` with `orphanRemoval = true` for automatic child entity management
- **Validation**: Request DTOs use Jakarta Bean Validation (`@NotBlank`, `@NotEmpty`, `@NotNull`, `@Valid`)
- **Constructor-based DI**: Services use Lombok's `@RequiredArgsConstructor` for dependency injection

### Mock Implementation Details

**Image Server 연동**
- 실제 Image Server는 아직 구현되지 않음
- 모든 API는 Mock 데이터로 동작
- `DetectService`: 랜덤 감지 결과 생성 (70% 확률, 1-2개 per category)
- `EditService`: UUID.randomUUID()로 newUuid 생성
- `ImageUrlUtil`: UUID를 `{image-server.url}/images/{uuid}` 형식으로 변환

**실제 Image Server 연동 시 수정 필요**
1. `DetectService`, `EditService`의 주석 처리된 실제 구현 코드 활성화
2. Mock 로직 제거
3. `application.yaml`의 `image-server.url` 업데이트

## Coding Conventions

### Code Style
- **클래스 선언**: 클래스명 다음 줄에 빈 줄 추가
  ```java
  public class Example {

      private String field;
  }
  ```

- **주석**: 한글 주석 사용
- **로깅**: 한글 로그 메시지 사용
- **DTO 생성**: 생성자 사용 (Builder 패턴 사용 금지)
  ```java
  // Good
  return new DetectResponse(uuid, detections, totalDetections);

  // Bad - Builder 패턴 사용하지 않음
  return DetectResponse.builder()
      .imageUuid(uuid)
      .build();
  ```

### Validation
- Request DTOs에 Jakarta Bean Validation 적용
- Controller에서 `@Valid` 어노테이션 사용
- DTO 필드에 validation 메시지 한글로 작성
  ```java
  @NotBlank(message = "imageUuid는 필수입니다")
  private String imageUuid;
  ```

## Important Notes

- **Database DDL**: `validate` 모드 사용 중. 스키마 변경 시 수동으로 마이그레이션 필요
- **Image Server**: 현재 Mock 구현. 실제 서버 연동 시 Service 계층 수정 필요
- **ImageUrlUtil**: UUID → URL 변환을 위한 공통 유틸리티. 모든 서비스에서 재사용
- **Transaction**: 읽기 작업에는 `@Transactional(readOnly = true)` 사용
- **API Spec**: `API.md` 파일에 상세 API 명세 문서화됨
