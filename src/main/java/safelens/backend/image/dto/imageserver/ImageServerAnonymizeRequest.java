package safelens.backend.image.dto.imageserver;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Image Server의 anonymize API 요청 DTO
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class ImageServerAnonymizeRequest {

    @JsonProperty("image_id")
    private String imageId;

    private List<ImageServerBboxWithLabel> regions;
    private String method;  // "generate", "blur", "mosaic"
}
