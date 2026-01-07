# Safelens API Quick Reference (배포 전 공유용)

- 인증: JWT Bearer. 모든 보호된 API 호출 시 `Authorization: Bearer <token>` 필요.
- 인가: `/history/{memberId}`, `/history/detail/{historyId}`는 소유자(토큰의 회원)만 접근 가능.
- 시간 형식: ISO-8601 (`2024-01-01T12:34:56`).

## 1) 이미지 업로드

### `POST /images/upload` (multipart/form-data, 인증 필요)

### Request

- `file`: 이미지 파일 (jpeg/png)

### Response 예시

```json
{
  "imageUuid": "7a2f4c5e-1234-4c3d-9b8a-abcdef123456"
}
```

## 2) 개인정보 감지

### `POST /detect` (application/json, 인증 필요)

### Request 예시

```json
{
  "imageUuid": "7a2f4c5e-1234-4c3d-9b8a-abcdef123456",
  "detectTargets": [
    "FACE",
    "TEXT",
    "LOCATION"
  ]
}
```

### Response 예시

```json
{
  "imageUuid": "7a2f4c5e-1234-4c3d-9b8a-abcdef123456",
  "detections": [
    {
      "category": "FACE",
      "pii_type": "face",
      "x": 120,
      "y": 80,
      "width": 160,
      "height": 160
    }
  ],
  "totalDetections": 1
}
```

## 3) 이미지 편집(마스킹/AI)

### `POST /edit` (application/json, 인증 필요)

### Request 예시

```json
{
  "imageUuid": "7a2f4c5e-1234-4c3d-9b8a-abcdef123456",
  "memberId": 1,
  "regions": [
    {
      "x": 120,
      "y": 80,
      "width": 160,
      "height": 160,
      "category": "FACE",
      "piiType": "face"
    }
  ],
  "filter": "BLUR"
  // BLUR | MOSAIC | AI
}
```

### Response 예시

```json
{
  "historyId": 10,
  "newUrl": "https://image.server/low-quality/edited.jpg",
  "oldUuid": "7a2f4c5e-1234-4c3d-9b8a-abcdef123456",
  "newUuid": "ed45c5ef-5678-4e3b-9c0d-fedcba654321",
  "filter": "BLUR",
  "editedRegions": [
    {
      "x": 120,
      "y": 80,
      "width": 160,
      "height": 160,
      "category": "FACE",
      "piiType": "face"
    }
  ],
  "createdAt": "2024-01-01T12:34:56"
}
```

## 4) 회원가입

### `POST /auth/signup` (application/json)

### Request 예시

```json
{
  "username": "user1",
  // min 4, max 10
  "password": "pass1234",
  // min 4, max 100
  "nickname": "닉네임"
  // min 2, max 20
}
```

### Response 예시

```json
{
  "token": "jwt-token-string",
  "username": "user1",
  "nickname": "닉네임"
}
```

## 5) 로그인

### `POST /auth/login` (application/json)

### Request 예시

```json
{
  "username": "user1",
  "password": "pass1234"
}
```

### Response 예시

```json
{
  "token": "jwt-token-string",
  "username": "user1",
  "nickname": "닉네임"
}
```

## 6) 히스토리 목록 조회 (인가)

### `GET /history/{memberId}` (인증 필요, 본인만 조회 가능)

### Response 예시

```json
{
  "memberMeId": 1,
  "nickname": "닉네임",
  "totalHistories": 2,
  "histories": [
    {
      "historyId": 10,
      "oldUrl": "https://image.server/low-quality/original.jpg",
      "newUrl": "https://image.server/low-quality/edited.jpg",
      "oldUuid": "orig-uuid",
      "newUuid": "edited-uuid",
      "filter": "BLUR",
      "createdAt": "2024-01-01T12:34:56",
      "detections": [
        {
          "id": 1,
          "category": "FACE",
          "x": 120,
          "y": 80,
          "width": 160,
          "height": 160
        }
      ]
    }
  ]
}
```

## 7) 히스토리 상세 조회 (인가)

### `GET /history/detail/{historyId}` (인증 필요, 히스토리 소유자만 조회)

### Response 예시

```json
{
  "historyId": 10,
  "memberId": 1,
  "imageUuid": "orig-uuid",
  "editedImageUrl": "https://image.server/low-quality/edited.jpg",
  "filter": "BLUR",
  "createdAt": "2024-01-01T12:34:56",
  "detections": [
    {
      "id": 1,
      "category": "FACE",
      "x": 120,
      "y": 80,
      "width": 160,
      "height": 160
    }
  ]
}
```

