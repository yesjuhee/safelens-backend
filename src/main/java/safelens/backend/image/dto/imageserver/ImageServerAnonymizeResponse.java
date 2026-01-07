package safelens.backend.image.dto.imageserver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Image Server의 anonymize API 응답 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ImageServerAnonymizeResponse {

    @JsonProperty("original_image_id")
    private String originalImageId;

    @JsonProperty("anonymized_image_id")
    private String anonymizedImageId;

    private Boolean success;
    private String message;

    @JsonProperty("processed_count")
    private Integer processedCount;
}
