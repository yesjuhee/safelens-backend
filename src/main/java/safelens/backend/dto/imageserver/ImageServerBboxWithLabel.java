package safelens.backend.dto.imageserver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Image Server의 anonymize 요청에 사용되는 bbox + pii_type DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ImageServerBboxWithLabel {

    private ImageServerBoundingBox bbox;

    @JsonProperty("pii_type")
    private String piiType;  // "face", "text", "location", "qrcode", "other"
}
