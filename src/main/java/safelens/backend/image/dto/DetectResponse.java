package safelens.backend.image.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import safelens.backend.history.dto.DetectionInfo;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DetectResponse {

    private String imageUuid;
    private List<DetectionInfo> detections;
    private Integer totalDetections;
}
