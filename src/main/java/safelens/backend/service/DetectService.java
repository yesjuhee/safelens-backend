package safelens.backend.service;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import safelens.backend.domain.Detect.CategoryType;
import safelens.backend.dto.DetectRequest;
import safelens.backend.dto.DetectResponse;
import safelens.backend.dto.DetectionInfo;
import safelens.backend.dto.imageserver.FaceDetection;
import safelens.backend.dto.imageserver.ImageServerDetectResponse;
import safelens.backend.dto.imageserver.PIIDetection;

@Slf4j
@Service
@RequiredArgsConstructor
public class DetectService {

    private final RestTemplate restTemplate;

    @Value("${image-server.url}")
    private String imageServerUrl;

    /**
     * Image Server를 호출하여 개인정보 감지 수행
     */
    public DetectResponse detectPersonalInfo(DetectRequest request) {
        log.info("Image Server 호출 시작 - imageUuid: {}", request.getImageUuid());
        log.info("감지 대상: {}", request.getDetectTargets());

        try {
            // Image Server 호출
            String url = imageServerUrl + "/detect/" + request.getImageUuid();

            ResponseEntity<ImageServerDetectResponse> response = restTemplate.postForEntity(
                    url,
                    null,  // 요청 바디 없음
                    ImageServerDetectResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Image Server가 정상 응답을 반환하지 않았습니다");
            }

            ImageServerDetectResponse imageServerResponse = response.getBody();

            // 응답 변환 및 필터링
            List<DetectionInfo> detections = transformToDetectionInfo(
                    imageServerResponse,
                    request.getDetectTargets()
            );

            log.info("감지 완료 - 총 {}개 영역 발견", detections.size());

            return new DetectResponse(
                    request.getImageUuid(),
                    detections,
                    detections.size()
            );

        } catch (Exception e) {
            log.error("Image Server 호출 중 오류 발생", e);
            throw new RuntimeException("개인정보 감지에 실패했습니다", e);
        }
    }

    /**
     * Image Server 응답을 백엔드 형식으로 변환하고 detectTargets로 필터링
     */
    private List<DetectionInfo> transformToDetectionInfo(
            ImageServerDetectResponse imageServerResponse,
            List<CategoryType> detectTargets
    ) {
        List<DetectionInfo> allDetections = new ArrayList<>();

        // PII 감지 결과 변환
        for (PIIDetection pii : imageServerResponse.getPiiDetections()) {
            CategoryType category = mapPiiTypeToCategory(pii.getPiiType());

            // detectTargets에 포함된 카테고리만 추가
            if (detectTargets.contains(category)) {
                allDetections.add(new DetectionInfo(
                        category,
                        pii.getPiiType(),
                        pii.getBbox().getX(),
                        pii.getBbox().getY(),
                        pii.getBbox().getWidth(),
                        pii.getBbox().getHeight()
                ));
            }
        }

        // 얼굴 감지 결과 변환
        if (detectTargets.contains(CategoryType.FACE)) {
            for (FaceDetection face : imageServerResponse.getFaceDetections()) {
                allDetections.add(new DetectionInfo(
                        CategoryType.FACE,
                        "face",
                        face.getBbox().getX(),
                        face.getBbox().getY(),
                        face.getBbox().getWidth(),
                        face.getBbox().getHeight()
                ));
            }
        }

        return allDetections;
    }

    /**
     * Image Server의 PII 타입을 백엔드 CategoryType으로 매핑
     */
    private CategoryType mapPiiTypeToCategory(String piiType) {
        return switch (piiType) {
            case "phone", "email", "name", "id_number", "credit_card", "date_of_birth", "license_plate" ->
                    CategoryType.TEXT;
            case "address", "signboard" -> CategoryType.LOCATION;
            case "qrcode", "barcode" -> CategoryType.QRBARCODE;
            case "other" -> CategoryType.ETC;
            default -> CategoryType.ETC;
        };
    }
}
