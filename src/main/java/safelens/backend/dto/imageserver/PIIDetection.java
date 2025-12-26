package safelens.backend.dto.imageserver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Image Server의 PII 감지 결과 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PIIDetection {

    @JsonProperty("detection_id")
    private String detectionId;

    @JsonProperty("detection_type")
    private String detectionType;

    @JsonProperty("pii_type")
    private String piiType;

    private String text;
    private BoundingBox bbox;
    private Double confidence;
}
