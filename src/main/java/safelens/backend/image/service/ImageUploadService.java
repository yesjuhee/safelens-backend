package safelens.backend.image.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import safelens.backend.image.dto.ImageUploadResponse;
import safelens.backend.image.dto.imageserver.ImageServerUploadResponse;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final RestTemplate restTemplate;

    @Value("${image-server.url}")
    private String imageServerUrl;

    /**
     * 이미지를 Image Server에 업로드하고 UUID 반환
     */
    public ImageUploadResponse uploadImage(MultipartFile file) {
        log.info("Image Server로 이미지 업로드 시작 - 파일명: {}", file.getOriginalFilename());

        // 파일 검증
        validateImageFile(file);

        try {
            // MultipartFile을 Image Server로 전송
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", file.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            // Image Server 호출
            ResponseEntity<ImageServerUploadResponse> response = restTemplate.postForEntity(
                    imageServerUrl + "/upload",
                    requestEntity,
                    ImageServerUploadResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Image Server가 정상 응답을 반환하지 않았습니다");
            }

            ImageServerUploadResponse serverResponse = response.getBody();
            log.info("이미지 업로드 완료 - imageUuid: {}, 메시지: {}",
                    serverResponse.getImageId(), serverResponse.getMessage());

            // 백엔드 응답 형식으로 변환 (image_id → imageUuid)
            return new ImageUploadResponse(serverResponse.getImageId());

        } catch (Exception e) {
            log.error("Image Server 업로드 중 오류 발생", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 업로드에 실패했습니다", e);
        }
    }

    /**
     * 이미지 파일 유효성 검증
     */
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("업로드할 파일이 비어있습니다");
        }

        String contentType = file.getContentType();
        if (contentType == null ||
                (!contentType.equals("image/jpeg") && !contentType.equals("image/png"))) {
            throw new RuntimeException("지원하지 않는 파일 형식입니다. JPEG 또는 PNG만 허용됩니다");
        }

        // 파일 크기 제한 (예: 10MB)
        long maxSize = 10 * 1024 * 1024; // 10MB
        if (file.getSize() > maxSize) {
            throw new RuntimeException("파일 크기가 너무 큽니다. 최대 10MB까지 허용됩니다");
        }
    }
}
