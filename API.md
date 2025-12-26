## API 명세

### 시스템 아키텍처

```
[Client]
↓
[Backend Server] ← → [MySQL DB]
↓
[Image Server] ← → [Vertex AI (Gemini + Imagen)]
```

- Backend Server: 클라이언트 요청 처리, DB 관리, 이미지 서버 중계
- Image Server: AI 기반 개인정보 감지 및 이미지 편집
- MySQL DB: 사용자 정보 및 편집 히스토리 관리

---

## 0. 이미지 업로드 API

### `POST /images/upload`

설명: 이미지 업로드

### 요청 (Cilent → Backend → Image Server 중계)

```
Content-Type: image/png, image/jpeg
// formData 로 보내기...
```

### 응답 (Image Server → Backend → Client)

```json
{
  "imageUuid": "550e8400-e29b-41d4-a716-446655440000"
}
```

## 1. 개인정보 감지 API

### `POST /detect`

**설명**: 이미지에서 개인정보 침해 가능 영역 감지

### **요청 (Backend → Image Server 중계)**

```json
{
  "imageUuid": "550e8400-e29b-41d4-a716-446655440000",
  "detectTargets": [
    "QRBARCODE",
    "TEXT",
    "LOCATION",
    "FACE"
  ]
}
```

### **응답 (Image Server → Backend → Client)**

```json
{
  "imageUuid": "550e8400-e29b-41d4-a716-446655440000",
  "detections": [
    {
      "category": "FACE",
      "x": 120,
      "y": 200,
      "width": 150,
      "height": 180
    },
    {
      "category": "TEXT",
      "x": 50,
      "y": 450,
      "width": 200,
      "height": 30
    },
    {
      "category": "QRBARCODE",
      "x": 300,
      "y": 100,
      "width": 80,
      "height": 80
    }
  ],
  "totalDetections": 3
}

```

---

## 2. 이미지 편집 API

### `POST /edit`

**설명**: 감지된 영역을 지정된 필터로 편집

### **요청 (Backend → Image Server 중계)**

```json
{
  "historyId": 789,
  "newUrl": "https://example.com/images/550e8400-e29b-41d4-a716-446655440001",
  "oldUuid": "550e8400-e29b-41d4-a716-446655440000",
  "newUuid": "550e8400-e29b-41d4-a716-446655440001",
  "filter": "AI",
  "editedRegions": [
    {
      "x": 120,
      "y": 200,
      "width": 150,
      "height": 180,
      "category": "FACE"
    },
    {
      "x": 50,
      "y": 450,
      "width": 200,
      "height": 30,
      "category": "TEXT"
    }
  ],
  "createdAt": "2025-12-26T14:30:25Z"
}
```

**처리 흐름**

1. Backend → Image Server 요청 전달
2. Image Server: 이미지 편집 수행
3. Image Server → Backend 편집된 이미지 URL 응답
4. Backend: History 및 Detect 정보 DB 저장
5. Backend → Client 응답 전송

**응답 (Image Server → Backend → Client)**

```json
{
  "historyId": 789,
  "originalImageUuid": "550e8400-e29b-41d4-a716-446655440000",
  "editedImageUuid": "550e8400-e29b-41d4-a716-446655440001",
  "filter": "AI",
  "editedRegions": [
    {
      "x": 120,
      "y": 200,
      "width": 150,
      "height": 180,
      "category": "FACE"
    },
    {
      "x": 50,
      "y": 450,
      "width": 200,
      "height": 30,
      "category": "TEXT"
    }
  ],
  "createdAt": "2025-12-26T14:30:25Z"
}

```

---

## 3. 히스토리 조회 API

### `GET /history/{memberId}`

**설명**: 특정 사용자의 전체 편집 히스토리 조회

**응답**

```json
{
  "memberMeId": 12345,
  "nickname": "member_nickname",
  "totalHistories": 15,
  "histories": [
    {
      "historyId": 789,
      "oldUrl": "https://example.com/images/550e8400-e29b-41d4-a716-446655440001",
      "newUrl": "https://example.com/images/550e8400-e29b-41d4-a716-446655440001",
      "oldUuid": "550e8400-e29b-41d4-a716-446655440000",
      "newUuid": "550e8400-e29b-41d4-a716-446655440001",
      "filter": "AI",
      "createdAt": "2025-12-26T14:30:25Z",
      "detections": [
        {
          "detectId": 1001,
          "category": "FACE",
          "x": 120,
          "y": 200,
          "width": 150,
          "height": 180
        },
        {
          "detectId": 1002,
          "category": "TEXT",
          "x": 50,
          "y": 450,
          "width": 200,
          "height": 30
        }
      ]
    },
    {
      "historyId": 788,
      "oldUuid": "550e8400-e29b-41d4-a716-446655440000",
      "newUuid": "550e8400-e29b-41d4-a716-446655440001",
      "filter": "BLUR",
      "createdAt": "2025-12-25T10:15:00Z",
      "detections": [
        {
          "detectId": 1000,
          "category": "QR",
          "x": 300,
          "y": 100,
          "width": 80,
          "height": 80
        }
      ]
    }
  ]
}

```

## 4. 단일 히스토리 상세 조회 API

### `GET /history/detail/{historyId}`

**설명**: 특정 히스토리의 상세 정보 조회

**요청**

```
GET /history/detail/789

```

**경로 파라미터**

| 파라미터      | 타입   | 필수 | 설명      |
|-----------|------|----|---------|
| historyId | Long | O  | 히스토리 ID |

**응답**

```json
{
  "historyId": 789,
  "memberId": 12345,
  "imageUuid": "550e8400-e29b-41d4-a716-446655440000",
  "editedImageUrl": "https://storage.example.com/edited/550e8400-edited-20251226.jpg",
  "filter": "AI",
  "createdAt": "2025-12-26T14:30:25Z",
  "detections": [
    {
      "detectId": 1001,
      "category": "FACE",
      "x": 120,
      "y": 200,
      "width": 150,
      "height": 180,
      "confidence": 0.9523
    },
    {
      "detectId": 1002,
      "category": "TEXT",
      "x": 50,
      "y": 450,
      "width": 200,
      "height": 30,
      "confidence": 0.8721
    }
  ]
}

```
