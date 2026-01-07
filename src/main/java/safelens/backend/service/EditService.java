package safelens.backend.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import safelens.backend.domain.Detect;
import safelens.backend.domain.Detect.CategoryType;
import safelens.backend.domain.History;
import safelens.backend.domain.History.FilterType;
import safelens.backend.domain.Member;
import safelens.backend.dto.EditRequest;
import safelens.backend.dto.EditResponse;
import safelens.backend.dto.EditedRegionInfo;
import safelens.backend.dto.RegionInfo;
import safelens.backend.dto.imageserver.ImageServerAnonymizeRequest;
import safelens.backend.dto.imageserver.ImageServerAnonymizeResponse;
import safelens.backend.dto.imageserver.ImageServerBboxWithLabel;
import safelens.backend.dto.imageserver.ImageServerBoundingBox;
import safelens.backend.repository.HistoryRepository;
import safelens.backend.repository.MemberRepository;
import safelens.backend.util.ImageUrlUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class EditService {

    private final MemberRepository memberRepository;
    private final HistoryRepository historyRepository;
    private final RestTemplate restTemplate;

    @Value("${image-server.url}")
    private String imageServerUrl;

    /**
     * 이미지 편집 처리 Image Server 호출 → DB 저장 → 응답 반환
     */
    @Transactional
    public EditResponse editImage(EditRequest request) {
        log.info("이미지 편집 처리 시작 - imageUuid: {}, memberId: {}",
                request.getImageUuid(), request.getMemberId());
        log.info("편집 영역 개수: {}, 필터: {}", request.getRegions().size(), request.getFilter());

        // 1. Member 조회
        Member member = memberRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Member not found: " + request.getMemberId()));

        // 2. 이미지 서버 요청 준비
        List<ImageServerBboxWithLabel> regions = request.getRegions().stream()
                .map(region -> {
                    ImageServerBoundingBox bbox = new ImageServerBoundingBox(
                            region.getX(),
                            region.getY(),
                            region.getWidth(),
                            region.getHeight()
                    );
                    return new ImageServerBboxWithLabel(bbox, region.getPiiType());
                })
                .collect(Collectors.toList());

        String method = mapFilterToMethod(request.getFilter());

        ImageServerAnonymizeRequest anonymizeRequest = new ImageServerAnonymizeRequest(
                request.getImageUuid(),
                regions,
                method
        );

        // 디버깅: 요청 내용 로깅
        logAnonymizeRequest(request.getImageUuid(), method, regions);

        // 3. Image Server 호출
        try {
            ResponseEntity<ImageServerAnonymizeResponse> response = restTemplate.postForEntity(
                    imageServerUrl + "/anonymize",
                    anonymizeRequest,
                    ImageServerAnonymizeResponse.class
            );

            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                throw new RuntimeException("Image Server가 정상 응답을 반환하지 않았습니다");
            }

            ImageServerAnonymizeResponse serverResponse = response.getBody();

            // 디버깅: 응답 내용 로깅
            logAnonymizeResponse(serverResponse);

            if (!serverResponse.getSuccess()) {
                throw new RuntimeException("이미지 편집에 실패했습니다: " + serverResponse.getMessage());
            }

            String newUuid = serverResponse.getAnonymizedImageId();
            String newUrl = ImageUrlUtil.toImageUrl(newUuid);

            log.info("이미지 편집 완료 - oldUuid: {}, newUuid: {}, 처리 영역: {}",
                    serverResponse.getOriginalImageId(), newUuid, serverResponse.getProcessedCount());

            // 4. History 엔티티 생성 및 저장
            History history = History.builder()
                    .member(member)
                    .oldUuid(request.getImageUuid())
                    .newUuid(newUuid)
                    .filter(request.getFilter())
                    .build();

            // 5. Detect 엔티티들 생성 및 History에 추가
            for (RegionInfo region : request.getRegions()) {
                Detect detect = Detect.builder()
                        .history(history)
                        .category(region.getCategory())
                        .x(region.getX())
                        .y(region.getY())
                        .width(region.getWidth())
                        .height(region.getHeight())
                        .build();
                history.addDetect(detect);
            }

            // 6. History 저장 (Cascade로 Detect도 함께 저장됨)
            History savedHistory = historyRepository.save(history);
            log.info("History 저장 완료 - historyId: {}", savedHistory.getId());

            // 7. 응답 생성
            List<EditedRegionInfo> editedRegions = request.getRegions().stream()
                    .map(region -> new EditedRegionInfo(
                            region.getX(),
                            region.getY(),
                            region.getWidth(),
                            region.getHeight(),
                            region.getCategory(),
                            region.getPiiType()
                    ))
                    .collect(Collectors.toList());

            return new EditResponse(
                    savedHistory.getId(),
                    newUrl,
                    savedHistory.getOldUuid(),
                    savedHistory.getNewUuid(),
                    savedHistory.getFilter(),
                    editedRegions,
                    savedHistory.getCreatedAt()
            );

        } catch (Exception e) {
            log.error("Image Server 호출 중 오류 발생", e);
            throw new RuntimeException("이미지 편집에 실패했습니다", e);
        }
    }

    /**
     * FilterType을 이미지 서버 method로 변환
     */
    private String mapFilterToMethod(FilterType filter) {
        return switch (filter) {
            case AI -> "generate";
            case BLUR -> "blur";
            case MOSAIC -> "mosaic";
        };
    }

    /**
     * CategoryType을 이미지 서버 type으로 변환
     */
    private String mapCategoryToType(CategoryType category) {
        return switch (category) {
            case FACE -> "face";
            case TEXT -> "text";
            case LOCATION -> "location";
            case QRBARCODE -> "qrcode";
            case ETC -> "other";
        };
    }

    /**
     * 이미지 서버 요청 내용 로깅 (디버깅용)
     */
    private void logAnonymizeRequest(String imageUuid, String method, List<ImageServerBboxWithLabel> regions) {
        log.info("=== Image Server 요청 상세 ===");
        log.info("요청 URL: {}", imageServerUrl + "/anonymize");
        log.info("image_id: {}", imageUuid);
        log.info("method: {}", method);
        log.info("regions 개수: {}", regions.size());
        for (int i = 0; i < regions.size(); i++) {
            ImageServerBboxWithLabel region = regions.get(i);
            log.info("Region[{}] - bbox: ({}, {}, {}, {}), pii_type: {}",
                    i,
                    region.getBbox().getX(),
                    region.getBbox().getY(),
                    region.getBbox().getWidth(),
                    region.getBbox().getHeight(),
                    region.getPiiType()
            );
        }
        log.info("===============================");
    }

    /**
     * 이미지 서버 응답 내용 로깅 (디버깅용)
     */
    private void logAnonymizeResponse(ImageServerAnonymizeResponse serverResponse) {
        log.info("=== Image Server 응답 상세 ===");
        log.info("success: {}", serverResponse.getSuccess());
        log.info("message: {}", serverResponse.getMessage());
        log.info("original_image_id: {}", serverResponse.getOriginalImageId());
        log.info("anonymized_image_id: {}", serverResponse.getAnonymizedImageId());
        log.info("processed_count: {}", serverResponse.getProcessedCount());
        log.info("===============================");
    }
}
