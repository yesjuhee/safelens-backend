package safelens.backend.history.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import safelens.backend.history.domain.Detect.CategoryType;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DetectionInfo {

    private CategoryType category;

    @JsonProperty("pii_type")
    private String piiType;

    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
}
