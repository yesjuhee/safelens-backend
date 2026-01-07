package safelens.backend.image.dto.imageserver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Image Server의 감지 API 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ImageServerDetectResponse {

    @JsonProperty("image_id")
    private String imageId;

    @JsonProperty("image_width")
    private Integer imageWidth;

    @JsonProperty("image_height")
    private Integer imageHeight;

    @JsonProperty("pii_detections")
    private List<PIIDetection> piiDetections;

    @JsonProperty("face_detections")
    private List<FaceDetection> faceDetections;
}
