package safelens.backend.history.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import safelens.backend.history.domain.History.FilterType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 단일 히스토리 상세 조회 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class HistoryDetailResponse {

    private Long historyId;
    private Long memberId;
    private String imageUuid;  // 원본 이미지 UUID (oldUuid)
    private String editedImageUrl;  // 편집된 이미지 URL
    private FilterType filter;
    private LocalDateTime createdAt;
    private List<DetectionDetailInfo> detections;
}
